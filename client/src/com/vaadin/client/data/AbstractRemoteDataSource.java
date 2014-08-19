/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.Profiler;
import com.vaadin.shared.ui.grid.Range;

/**
 * Base implementation for data sources that fetch data from a remote system.
 * This class takes care of caching data and communicating with the data source
 * user. An implementation of this class should override
 * {@link #requestRows(int, int)} to trigger asynchronously loading of data.
 * When data is received from the server, new row data should be passed to
 * {@link #setRowData(int, List)}. {@link #setEstimatedSize(int)} should be used
 * based on estimations of how many rows are available.
 * 
 * @since
 * @author Vaadin Ltd
 * @param <T>
 *            the row type
 */
public abstract class AbstractRemoteDataSource<T> implements DataSource<T> {

    protected class RowHandleImpl extends RowHandle<T> {
        private T row;
        private final Object key;

        public RowHandleImpl(final T row, final Object key) {
            this.row = row;
            this.key = key;
        }

        /**
         * A method for the data source to update the row data.
         * 
         * @param row
         *            the updated row object
         */
        public void setRow(final T row) {
            this.row = row;
            assert getRowKey(row).equals(key) : "The old key does not "
                    + "equal the new key for the given row (old: " + key
                    + ", new :" + getRowKey(row) + ")";
        }

        @Override
        public T getRow() throws IllegalStateException {
            if (isPinned()) {
                return row;
            } else {
                throw new IllegalStateException("The row handle for key " + key
                        + " was not pinned");
            }
        }

        private boolean isPinned() {
            return pinnedRows.containsKey(key);
        }

        @Override
        public void pin() {
            Integer count = pinnedCounts.get(key);
            if (count == null) {
                count = Integer.valueOf(0);
                pinnedRows.put(key, this);
            }
            pinnedCounts.put(key, Integer.valueOf(count.intValue() + 1));
        }

        @Override
        public void unpin() throws IllegalStateException {
            final Integer count = pinnedCounts.get(key);
            if (count == null) {
                throw new IllegalStateException("Row " + row + " with key "
                        + key + " was not pinned to begin with");
            } else if (count.equals(Integer.valueOf(1))) {
                pinnedRows.remove(key);
                pinnedCounts.remove(key);
            } else {
                pinnedCounts.put(key, Integer.valueOf(count.intValue() - 1));
            }
        }

        @Override
        protected boolean equalsExplicit(final Object obj) {
            if (obj instanceof AbstractRemoteDataSource.RowHandleImpl) {
                /*
                 * Java prefers AbstractRemoteDataSource<?>.RowHandleImpl. I
                 * like the @SuppressWarnings more (keeps the line length in
                 * check.)
                 */
                @SuppressWarnings("unchecked")
                final RowHandleImpl rhi = (RowHandleImpl) obj;
                return key.equals(rhi.key);
            } else {
                return false;
            }
        }

        @Override
        protected int hashCodeExplicit() {
            return key.hashCode();
        }
    }

    /**
     * Records the start of the previously requested range. This is used when
     * tracking request timings to distinguish between explicit responses and
     * arbitrary updates pushed from the server.
     */
    private int lastRequestStart = -1;
    private double pendingRequestTime;

    private boolean coverageCheckPending = false;

    private Range requestedAvailability = Range.between(0, 0);

    private Range cached = Range.between(0, 0);

    private final HashMap<Integer, T> rowCache = new HashMap<Integer, T>();

    private DataChangeHandler dataChangeHandler;

    private Range estimatedAvailableRange = Range.between(0, 0);

    private CacheStrategy cacheStrategy = new CacheStrategy.DefaultCacheStrategy();

    private final ScheduledCommand coverageChecker = new ScheduledCommand() {
        @Override
        public void execute() {
            coverageCheckPending = false;
            checkCacheCoverage();
        }
    };

    private Map<Object, Integer> pinnedCounts = new HashMap<Object, Integer>();
    private Map<Object, RowHandleImpl> pinnedRows = new HashMap<Object, RowHandleImpl>();
    protected Collection<T> temporarilyPinnedRows = Collections.emptySet();

    /**
     * Sets the estimated number of rows in the data source.
     * 
     * @param estimatedSize
     *            the estimated number of available rows
     */
    protected void setEstimatedSize(int estimatedSize) {
        // TODO update dataChangeHandler if size changes
        estimatedAvailableRange = Range.withLength(0, estimatedSize);
    }

    private void ensureCoverageCheck() {
        if (!coverageCheckPending) {
            coverageCheckPending = true;
            Scheduler.get().scheduleDeferred(coverageChecker);
        }
    }

    @Override
    public void ensureAvailability(int firstRowIndex, int numberOfRows) {
        requestedAvailability = Range.withLength(firstRowIndex, numberOfRows);

        /*
         * Don't request any data right away since the data might be included in
         * a message that has been received but not yet fully processed.
         */
        ensureCoverageCheck();
    }

    private void checkCacheCoverage() {
        if (lastRequestStart != -1) {
            // Anyone clearing lastRequestStart should run this method again
            return;
        }

        Profiler.enter("AbstractRemoteDataSource.checkCacheCoverage");

        Range minCacheRange = getMinCacheRange();

        if (!minCacheRange.intersects(cached) || cached.isEmpty()) {
            /*
             * Simple case: no overlap between cached data and needed data.
             * Clear the cache and request new data
             */
            rowCache.clear();
            cached = Range.between(0, 0);

            handleMissingRows(getMaxCacheRange());
        } else {
            discardStaleCacheEntries();

            // Might need more rows -> request them
            if (!minCacheRange.isSubsetOf(cached)) {
                Range[] missingCachePartition = getMaxCacheRange()
                        .partitionWith(cached);
                handleMissingRows(missingCachePartition[0]);
                handleMissingRows(missingCachePartition[2]);
            }
        }

        Profiler.leave("AbstractRemoteDataSource.checkCacheCoverage");
    }

    private void discardStaleCacheEntries() {
        Range[] cacheParition = cached.partitionWith(getMaxCacheRange());
        dropFromCache(cacheParition[0]);
        cached = cacheParition[1];
        dropFromCache(cacheParition[2]);
    }

    private void dropFromCache(Range range) {
        for (int i = range.getStart(); i < range.getEnd(); i++) {
            rowCache.remove(Integer.valueOf(i));
        }
    }

    private void handleMissingRows(Range range) {
        if (range.isEmpty()) {
            return;
        }
        lastRequestStart = range.getStart();
        pendingRequestTime = Duration.currentTimeMillis();
        requestRows(range.getStart(), range.length());
    }

    /**
     * Triggers fetching rows from the remote data source.
     * {@link #setRowData(int, List)} should be invoked with data for the
     * requested rows when they have been received.
     * 
     * @param firstRowIndex
     *            the index of the first row to fetch
     * @param numberOfRows
     *            the number of rows to fetch
     */
    protected abstract void requestRows(int firstRowIndex, int numberOfRows);

    @Override
    public int getEstimatedSize() {
        return estimatedAvailableRange.length();
    }

    @Override
    public T getRow(int rowIndex) {
        return rowCache.get(Integer.valueOf(rowIndex));
    }

    @Override
    public void setDataChangeHandler(DataChangeHandler dataChangeHandler) {
        this.dataChangeHandler = dataChangeHandler;

        if (dataChangeHandler != null && !cached.isEmpty()) {
            // Push currently cached data to the implementation
            dataChangeHandler.dataUpdated(cached.getStart(), cached.length());
            dataChangeHandler.dataAvailable(cached.getStart(), cached.length());
        }
    }

    /**
     * Informs this data source that updated data has been sent from the server.
     * 
     * @param firstRowIndex
     *            the index of the first received row
     * @param rowData
     *            a list of rows, starting from <code>firstRowIndex</code>
     */
    protected void setRowData(int firstRowIndex, List<T> rowData) {

        Profiler.enter("AbstractRemoteDataSource.setRowData");

        Range received = Range.withLength(firstRowIndex, rowData.size());

        if (firstRowIndex == lastRequestStart) {
            // Provide timing information if we know when we asked for this data
            cacheStrategy.onDataArrive(Duration.currentTimeMillis()
                    - pendingRequestTime, received.length());
        }
        lastRequestStart = -1;

        Range maxCacheRange = getMaxCacheRange();

        Range[] partition = received.partitionWith(maxCacheRange);

        Range newUsefulData = partition[1];
        if (!newUsefulData.isEmpty()) {
            // Update the parts that are actually inside
            for (int i = newUsefulData.getStart(); i < newUsefulData.getEnd(); i++) {
                rowCache.put(Integer.valueOf(i), rowData.get(i - firstRowIndex));
            }

            if (dataChangeHandler != null) {
                Profiler.enter("AbstractRemoteDataSource.setRowData notify dataChangeHandler");
                dataChangeHandler.dataUpdated(newUsefulData.getStart(),
                        newUsefulData.length());
                Profiler.leave("AbstractRemoteDataSource.setRowData notify dataChangeHandler");
            }

            // Potentially extend the range
            if (cached.isEmpty()) {
                cached = newUsefulData;
            } else {
                discardStaleCacheEntries();

                /*
                 * everything might've become stale so we need to re-check for
                 * emptiness.
                 */
                if (!cached.isEmpty()) {
                    cached = cached.combineWith(newUsefulData);
                } else {
                    cached = newUsefulData;
                }
            }
            dataChangeHandler.dataAvailable(cached.getStart(), cached.length());

            updatePinnedRows(rowData);
        }

        if (!partition[0].isEmpty() || !partition[2].isEmpty()) {
            /*
             * FIXME
             * 
             * Got data that we might need in a moment if the container is
             * updated before the widget settings. Support for this will be
             * implemented later on.
             */
        }

        // Eventually check whether all needed rows are now available
        ensureCoverageCheck();

        Profiler.leave("AbstractRemoteDataSource.setRowData");
    }

    private void updatePinnedRows(final List<T> rowData) {
        for (final T row : rowData) {
            final Object key = getRowKey(row);
            final RowHandleImpl handle = pinnedRows.get(key);
            if (handle != null) {
                handle.setRow(row);
            }
        }
    }

    /**
     * Informs this data source that the server has removed data.
     * 
     * @param firstRowIndex
     *            the index of the first removed row
     * @param count
     *            the number of removed rows, starting from
     *            <code>firstRowIndex</code>
     */
    protected void removeRowData(int firstRowIndex, int count) {
        Profiler.enter("AbstractRemoteDataSource.removeRowData");

        // pack the cached data
        for (int i = 0; i < count; i++) {
            Integer oldIndex = Integer.valueOf(firstRowIndex + count + i);
            if (rowCache.containsKey(oldIndex)) {
                Integer newIndex = Integer.valueOf(firstRowIndex + i);
                rowCache.put(newIndex, rowCache.remove(oldIndex));
            }
        }

        Range removedRange = Range.withLength(firstRowIndex, count);
        if (cached.isSubsetOf(removedRange)) {
            cached = Range.withLength(0, 0);
        } else if (removedRange.intersects(cached)) {
            Range[] partitions = cached.partitionWith(removedRange);
            Range remainsBefore = partitions[0];
            Range transposedRemainsAfter = partitions[2].offsetBy(-removedRange
                    .length());
            cached = remainsBefore.combineWith(transposedRemainsAfter);
        }
        setEstimatedSize(getEstimatedSize() - count);
        dataChangeHandler.dataRemoved(firstRowIndex, count);
        checkCacheCoverage();

        Profiler.leave("AbstractRemoteDataSource.removeRowData");
    }

    /**
     * Informs this data source that new data has been inserted from the server.
     * 
     * @param firstRowIndex
     *            the destination index of the new row data
     * @param count
     *            the number of rows inserted
     */
    protected void insertRowData(int firstRowIndex, int count) {
        Profiler.enter("AbstractRemoteDataSource.insertRowData");

        if (cached.contains(firstRowIndex)) {
            int oldCacheEnd = cached.getEnd();
            /*
             * We need to invalidate the cache from the inserted row onwards,
             * since the cache wants to be a contiguous range. It doesn't
             * support holes.
             * 
             * If holes were supported, we could shift the higher part of
             * "cached" and leave a hole the size of "count" in the middle.
             */
            cached = cached.splitAt(firstRowIndex)[0];

            for (int i = firstRowIndex; i < oldCacheEnd; i++) {
                rowCache.remove(Integer.valueOf(i));
            }
        }

        else if (firstRowIndex < cached.getStart()) {
            Range oldCached = cached;
            cached = cached.offsetBy(count);

            for (int i = 0; i < rowCache.size(); i++) {
                Integer oldIndex = Integer.valueOf(oldCached.getEnd() - i);
                Integer newIndex = Integer.valueOf(cached.getEnd() - i);
                rowCache.put(newIndex, rowCache.remove(oldIndex));
            }
        }

        setEstimatedSize(getEstimatedSize() + count);
        dataChangeHandler.dataAdded(firstRowIndex, count);
        checkCacheCoverage();

        Profiler.leave("AbstractRemoteDataSource.insertRowData");
    }

    /**
     * Gets the current range of cached rows
     * 
     * @return the range of currently cached rows
     */
    public Range getCachedRange() {
        return cached;
    }

    /**
     * Sets the cache strategy that is used to determine how much data is
     * fetched and cached.
     * <p>
     * The new strategy is immediately used to evaluate whether currently cached
     * rows should be discarded or new rows should be fetched.
     * 
     * @param cacheStrategy
     *            a cache strategy implementation, not <code>null</code>
     */
    public void setCacheStrategy(CacheStrategy cacheStrategy) {
        if (cacheStrategy == null) {
            throw new IllegalArgumentException();
        }

        if (this.cacheStrategy != cacheStrategy) {
            this.cacheStrategy = cacheStrategy;

            checkCacheCoverage();
        }
    }

    private Range getMinCacheRange() {
        Range minCacheRange = cacheStrategy.getMinCacheRange(
                requestedAvailability, cached, estimatedAvailableRange);

        assert minCacheRange.isSubsetOf(estimatedAvailableRange);

        return minCacheRange;
    }

    private Range getMaxCacheRange() {
        Range maxCacheRange = cacheStrategy.getMaxCacheRange(
                requestedAvailability, cached, estimatedAvailableRange);

        assert maxCacheRange.isSubsetOf(estimatedAvailableRange);

        return maxCacheRange;
    }

    @Override
    public RowHandle<T> getHandle(T row) throws IllegalStateException {
        Object key = getRowKey(row);

        if (key == null) {
            throw new NullPointerException("key may not be null (row: " + row
                    + ")");
        }

        if (pinnedRows.containsKey(key)) {
            return pinnedRows.get(key);
        } else if (rowCache.containsValue(row)) {
            return new RowHandleImpl(row, key);
        } else {
            throw new IllegalStateException("The cache of this DataSource "
                    + "does not currently contain the row " + row);
        }
    }

    /**
     * Gets a stable key for the row object.
     * <p>
     * This method is a workaround for the fact that there is no means to force
     * proper implementations for {@link #hashCode()} and
     * {@link #equals(Object)} methods.
     * <p>
     * Since the same row object will be created several times for the same
     * logical data, the DataSource needs a mechanism to be able to compare two
     * objects, and figure out whether or not they represent the same data. Even
     * if all the fields of an entity would be changed, it still could represent
     * the very same thing (say, a person changes all of her names.)
     * <p>
     * A very usual and simple example what this could be, is an unique ID for
     * this object that would also be stored in a database.
     * 
     * @param row
     *            the row object for which to get the key
     * @return a non-null object that uniquely and consistently represents the
     *         row object
     */
    abstract public Object getRowKey(T row);

    /**
     * Marks rows as pinned when fetching new rows.
     * <p>
     * This collection of rows are intended to remain pinned if new rows are
     * fetched from the data source, even if some of the pinned rows would fall
     * off the cache and become inactive.
     * <p>
     * This method does nothing by itself, other than it stores the rows into a
     * field. The implementation needs to make all the adjustments for itself.
     * Check {@link RpcDataSourceConnector.RpcDataSource#requestRows(int, int)}
     * for an implementation example.
     * 
     * @param keys
     *            a collection of rows to keep pinned
     * 
     * @see #temporarilyPinnedRows
     * @see RpcDataSourceConnector.RpcDataSource#requestRows(int, int)
     * @deprecated You probably don't want to call this method unless you're
     *             writing a Renderer for a selection model. Even if you are, be
     *             very aware what this method does and how it behaves.
     */
    @Deprecated
    public void transactionPin(Collection<T> rows) {
        if (rows == null) {
            throw new IllegalArgumentException("argument may not be null");
        }
        temporarilyPinnedRows = rows;
    }
}
