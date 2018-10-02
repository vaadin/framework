/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.vaadin.client.Profiler;
import com.vaadin.shared.Range;
import com.vaadin.shared.Registration;

/**
 * Base implementation for data sources that fetch data from a remote system.
 * This class takes care of caching data and communicating with the data source
 * user. An implementation of this class should override
 * {@link #requestRows(int, int, RequestRowsCallback)} to trigger asynchronously
 * loading of data and then pass the loaded data into the provided callback.
 *
 * @since 7.4
 * @author Vaadin Ltd
 * @param <T>
 *            the row type
 */
public abstract class AbstractRemoteDataSource<T> implements DataSource<T> {

    /**
     * Callback used by
     * {@link AbstractRemoteDataSource#requestRows(int, int, RequestRowsCallback)}
     * to pass data to the underlying implementation when data has been fetched.
     *
     * @param <T>
     *            the row type
     */
    public static class RequestRowsCallback<T> {
        private final Range requestedRange;
        private final double requestStart;
        private final AbstractRemoteDataSource<T> source;

        /**
         * Creates a new callback.
         *
         * @param source
         *            the data source for which the request is made
         * @param requestedRange
         *            the requested row range
         */
        protected RequestRowsCallback(AbstractRemoteDataSource<T> source,
                Range requestedRange) {
            this.source = source;
            this.requestedRange = requestedRange;

            requestStart = Duration.currentTimeMillis();
        }

        /**
         * Called by the
         * {@link AbstractRemoteDataSource#requestRows(int, int, RequestRowsCallback)}
         * implementation when data has been received.
         *
         * @param rowData
         *            a list of row objects starting at the requested offset
         * @param totalSize
         *            the total number of rows available at the remote end
         */
        public void onResponse(List<T> rowData, int totalSize) {
            if (source.size != totalSize) {
                source.resetDataAndSize(totalSize);
            }
            source.setRowData(requestedRange.getStart(), rowData);
        }

        /**
         * Gets the range of rows that was requested.
         *
         * @return the requsted row range
         */
        public Range getRequestedRange() {
            return requestedRange;
        }

    }

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
            return row;
        }

        public boolean isPinned() {
            return pinnedRows.containsKey(key);
        }

        @Override
        public void pin() {
            pinHandle(this);
        }

        @Override
        public void unpin() throws IllegalStateException {
            unpinHandle(this);
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

        @Override
        public void updateRow() {
            int index = indexOf(row);
            if (index >= 0) {
                getHandlers().forEach(dch -> dch.dataUpdated(index, 1));
            }
        }
    }

    private RequestRowsCallback<T> currentRequestCallback;

    private boolean coverageCheckPending = false;

    private Range requestedAvailability = Range.between(0, 0);

    private Range cached = Range.between(0, 0);

    private final Map<Integer, T> indexToRowMap = new HashMap<>();
    private final Map<Object, Integer> keyToIndexMap = new HashMap<>();

    /**
     * Map used to temporarily store rows invalidated by
     * {@link #insertRowData(int, int)}. All invalidated rows are stored with
     * their indices matching the state after row insertion. If the backend has
     * pre-emptively pushed the row data for the added rows, these rows will be
     * used again to fill the cache.
     * <p>
     * To avoid this cache invalidation issue from getting too big, this class
     * does not attempt to track these rows indefinitely. Multiple row
     * manipulations without filling the gaps in between will remove all
     * invalidated rows and prevent any attempts to restore them. This is
     * indicated by having the map empty, not {@code null}.
     * <p>
     * The map is set to {@code null} upon requesting the rows from the backend
     * to indicate that future row additions can attempt to restore the cache.
     */
    private Map<Integer, T> invalidatedRows;

    private Set<DataChangeHandler> dataChangeHandlers = new LinkedHashSet<>();

    private CacheStrategy cacheStrategy = new CacheStrategy.DefaultCacheStrategy();

    private final ScheduledCommand coverageChecker = () -> {
        coverageCheckPending = false;
        checkCacheCoverage();
    };

    private Map<Object, Integer> pinnedCounts = new HashMap<>();
    private Map<Object, RowHandleImpl> pinnedRows = new HashMap<>();

    // Size not yet known
    private int size = -1;

    private void ensureCoverageCheck() {
        if (!coverageCheckPending) {
            coverageCheckPending = true;
            Scheduler.get().scheduleDeferred(coverageChecker);
        }
    }

    /**
     * Pins a row with given handle. This function can be overridden to do
     * specific logic related to pinning rows.
     *
     * @param handle
     *            row handle to pin
     */
    protected void pinHandle(RowHandleImpl handle) {
        Object key = handle.key;
        Integer count = pinnedCounts.get(key);
        if (count == null) {
            count = Integer.valueOf(0);
            pinnedRows.put(key, handle);
        }
        pinnedCounts.put(key, Integer.valueOf(count.intValue() + 1));
    }

    /**
     * Unpins a previously pinned row with given handle. This function can be
     * overridden to do specific logic related to unpinning rows.
     *
     * @param handle
     *            row handle to unpin
     *
     * @throws IllegalStateException
     *             if given row handle has not been pinned before
     */
    protected void unpinHandle(RowHandleImpl handle)
            throws IllegalStateException {
        Object key = handle.key;
        final Integer count = pinnedCounts.get(key);
        if (count == null) {
            throw new IllegalStateException("Row " + handle.getRow()
                    + " with key " + key + " was not pinned to begin with");
        } else if (count.equals(Integer.valueOf(1))) {
            pinnedRows.remove(key);
            pinnedCounts.remove(key);
        } else {
            pinnedCounts.put(key, Integer.valueOf(count.intValue() - 1));
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

    /**
     * Gets the row index range that was requested by the previous call to
     * {@link #ensureAvailability(int, int)}.
     *
     * @return the requested availability range
     */
    public Range getRequestedAvailability() {
        return requestedAvailability;
    }

    private void checkCacheCoverage() {
        if (isWaitingForData()) {
            // Anyone clearing the waiting status should run this method again
            return;
        }

        Profiler.enter("AbstractRemoteDataSource.checkCacheCoverage");

        // Clean up invalidated data
        invalidatedRows = null;

        Range minCacheRange = getMinCacheRange();

        if (!minCacheRange.intersects(cached) || cached.isEmpty()) {
            /*
             * Simple case: no overlap between cached data and needed data.
             * Clear the cache and request new data
             */
            dropFromCache(cached);
            cached = Range.between(0, 0);

            Range maxCacheRange = getMaxCacheRange();
            if (!maxCacheRange.isEmpty()) {
                handleMissingRows(maxCacheRange);
            } else {
                // There is nothing to fetch. We're done here.
                getHandlers().forEach(dch -> dch
                        .dataAvailable(cached.getStart(), cached.length()));
            }
        } else {
            discardStaleCacheEntries();

            // Might need more rows -> request them
            if (!minCacheRange.isSubsetOf(cached)) {
                Range[] missingCachePartition = getMaxCacheRange()
                        .partitionWith(cached);
                handleMissingRows(missingCachePartition[0]);
                handleMissingRows(missingCachePartition[2]);
            } else {
                getHandlers().forEach(dch -> dch
                        .dataAvailable(cached.getStart(), cached.length()));
            }
        }

        Profiler.leave("AbstractRemoteDataSource.checkCacheCoverage");
    }

    /**
     * Checks whether this data source is currently waiting for more rows to
     * become available.
     *
     * @return <code>true</code> if waiting for data; otherwise
     *         <code>false</code>
     */
    @Override
    public boolean isWaitingForData() {
        return currentRequestCallback != null;
    }

    private void discardStaleCacheEntries() {
        Range[] cacheParition = cached.partitionWith(getMaxCacheRange());
        dropFromCache(cacheParition[0]);
        cached = cacheParition[1];
        dropFromCache(cacheParition[2]);
    }

    /**
     * Drop the given range of rows from this data source's cache.
     *
     * @param range
     *            the range of rows to drop
     */
    protected void dropFromCache(Range range) {
        for (int i = range.getStart(); i < range.getEnd(); i++) {
            // Called after dropping from cache. Dropped row is passed as a
            // parameter, but is no longer present in the DataSource
            T removed = indexToRowMap.remove(Integer.valueOf(i));
            if (removed != null) {
                onDropFromCache(i, removed);
                keyToIndexMap.remove(getRowKey(removed));
            }
        }
    }

    /**
     * A hook that can be overridden to do something whenever a row has been
     * dropped from the cache. DataSource no longer has anything in the given
     * index.
     * <p>
     * NOTE: This method has been replaced. Override
     * {@link #onDropFromCache(int, Object)} instead of this method.
     *
     * @since 7.5.0
     * @param rowIndex
     *            the index of the dropped row
     * @deprecated replaced by {@link #onDropFromCache(int, Object)}
     */
    @Deprecated
    protected void onDropFromCache(int rowIndex) {
        // noop
    }

    /**
     * A hook that can be overridden to do something whenever a row has been
     * dropped from the cache. DataSource no longer has anything in the given
     * index.
     *
     * @since 7.6
     * @param rowIndex
     *            the index of the dropped row
     * @param removed
     *            the removed row object
     */
    protected void onDropFromCache(int rowIndex, T removed) {
        // Call old version as a fallback (someone might have used it)
        onDropFromCache(rowIndex);
    }

    private void handleMissingRows(Range range) {
        if (range.isEmpty() || !canFetchData()) {
            return;
        }
        currentRequestCallback = new RequestRowsCallback<>(this, range);
        requestRows(range.getStart(), range.length(), currentRequestCallback);
    }

    /**
     * Triggers fetching rows from the remote data source. The provided callback
     * should be informed when the requested rows have been received.
     *
     * @param firstRowIndex
     *            the index of the first row to fetch
     * @param numberOfRows
     *            the number of rows to fetch
     * @param callback
     *            callback to inform when the requested rows are available
     */
    protected abstract void requestRows(int firstRowIndex, int numberOfRows,
            RequestRowsCallback<T> callback);

    @Override
    public T getRow(int rowIndex) {
        return indexToRowMap.get(Integer.valueOf(rowIndex));
    }

    /**
     * Retrieves the index for given row object.
     * <p>
     * <em>Note:</em> This method does not verify that the given row object
     * exists at all in this DataSource.
     *
     * @param row
     *            the row object
     * @return index of the row; or <code>-1</code> if row is not available
     */
    public int indexOf(T row) {
        Object key = getRowKey(row);
        if (keyToIndexMap.containsKey(key)) {
            return keyToIndexMap.get(key);
        }
        return -1;
    }

    @Override
    public Registration addDataChangeHandler(
            final DataChangeHandler dataChangeHandler) {
        Objects.requireNonNull(dataChangeHandler,
                "DataChangeHandler can't be null");
        dataChangeHandlers.add(dataChangeHandler);

        if (!cached.isEmpty()) {
            // Push currently cached data to the implementation
            dataChangeHandler.dataUpdated(cached.getStart(), cached.length());
            dataChangeHandler.dataAvailable(cached.getStart(), cached.length());
        }

        return () -> dataChangeHandlers.remove(dataChangeHandler);
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
        assert firstRowIndex + rowData.size() <= size();

        Profiler.enter("AbstractRemoteDataSource.setRowData");

        Range received = Range.withLength(firstRowIndex, rowData.size());

        if (isWaitingForData()) {
            cacheStrategy.onDataArrive(
                    Duration.currentTimeMillis()
                            - currentRequestCallback.requestStart,
                    received.length());

            currentRequestCallback = null;
        }

        Range maxCacheRange = getMaxCacheRange(received);

        Range[] partition = received.partitionWith(maxCacheRange);

        Range newUsefulData = partition[1];
        if (!newUsefulData.isEmpty()) {
            if (!cached.isEmpty())
                discardStaleCacheEntries();

            // Update the parts that are actually inside
            int start = newUsefulData.getStart();
            for (int i = start; i < newUsefulData.getEnd(); i++) {
                final T row = rowData.get(i - firstRowIndex);
                indexToRowMap.put(Integer.valueOf(i), row);
                keyToIndexMap.put(getRowKey(row), Integer.valueOf(i));
            }

            Profiler.enter(
                    "AbstractRemoteDataSource.setRowData notify dataChangeHandler");
            int length = newUsefulData.length();
            getHandlers().forEach(dch -> dch.dataUpdated(start, length));
            Profiler.leave(
                    "AbstractRemoteDataSource.setRowData notify dataChangeHandler");

            // Potentially extend the range
            if (cached.isEmpty()) {
                cached = newUsefulData;
            } else {

                /*
                 * everything might've become stale so we need to re-check for
                 * emptiness.
                 */
                if (!cached.isEmpty()) {
                    cached = cached.combineWith(newUsefulData);
                    // Attempt to restore invalidated items
                    fillCacheFromInvalidatedRows(maxCacheRange);
                } else {
                    cached = newUsefulData;
                }
            }

            getHandlers().forEach(dch -> dch.dataAvailable(cached.getStart(),
                    cached.length()));

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

            // Run a dummy drop from cache for unused rows.
            for (int i = 0; i < partition[0].length(); ++i) {
                onDropFromCache(i + partition[0].getStart(), rowData.get(i));
            }

            for (int i = 0; i < partition[2].length(); ++i) {
                onDropFromCache(i + partition[2].getStart(), rowData.get(i));
            }
        }

        // Eventually check whether all needed rows are now available
        ensureCoverageCheck();

        Profiler.leave("AbstractRemoteDataSource.setRowData");

    }

    /**
     * Go through items invalidated by {@link #insertRowData(int, int)}. If the
     * server has pre-emptively sent added row data immediately after informing
     * of row addition, the invalid cache can be restored to proper index range.
     *
     * @param maxCacheRange
     *            the maximum amount of rows that can cached
     */
    private void fillCacheFromInvalidatedRows(Range maxCacheRange) {
        if (invalidatedRows == null || invalidatedRows.isEmpty()) {
            // No old invalid cache available
            return;
        }

        Range potentialCache = maxCacheRange.partitionWith(cached)[2];
        int start = potentialCache.getStart();
        int last = start;
        try {
            if (potentialCache.isEmpty()
                    || invalidatedRows.containsKey(start - 1)) {
                // Cache is already full or invalidated rows contains unexpected
                // indices.
                return;
            }

            for (int i = start; i < potentialCache.getEnd(); ++i) {
                if (!invalidatedRows.containsKey(i)) {
                    return;
                }
                T row = invalidatedRows.get(i);
                indexToRowMap.put(i, row);
                keyToIndexMap.put(getRowKey(row), i);
                last = i;
            }

            // Cache filled from invalidated rows. Can continue as if it was
            // never invalidated.
            invalidatedRows = null;
        } finally {
            // Update cache range and clean up
            if (invalidatedRows != null) {
                invalidatedRows.clear();
            }

            Range updated = Range.between(start, last + 1);
            cached = cached.combineWith(updated);

            dataChangeHandlers.forEach(dch -> dch
                    .dataUpdated(updated.getStart(), updated.length()));
        }
    }

    private Stream<DataChangeHandler> getHandlers() {
        Set<DataChangeHandler> copy = new LinkedHashSet<>(dataChangeHandlers);
        return copy.stream();
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

        // Cache was not filled since previous insertRowData. The old rows are
        // no longer useful.
        if (invalidatedRows != null) {
            invalidatedRows.clear();
        }

        size -= count;

        Range removedRange = Range.withLength(firstRowIndex, count);
        dropFromCache(removedRange);

        // shift indices to fill the cache correctly
        int firstMoved = Math.max(firstRowIndex + count, cached.getStart());
        for (int i = firstMoved; i < cached.getEnd(); i++) {
            moveRowFromIndexToIndex(i, i - count);
        }

        if (cached.isSubsetOf(removedRange)) {
            // Whole cache is part of the removal. Empty cache
            cached = Range.withLength(0, 0);
        } else if (removedRange.intersects(cached)) {
            // Removal and cache share some indices. fix accordingly.
            Range[] partitions = cached.partitionWith(removedRange);
            Range remainsBefore = partitions[0];
            Range transposedRemainsAfter = partitions[2]
                    .offsetBy(-removedRange.length());
            // #8840 either can be empty if the removed range was over the
            // cached range
            if (remainsBefore.isEmpty()) {
                cached = transposedRemainsAfter;
            } else if (transposedRemainsAfter.isEmpty()) {
                cached = remainsBefore;
            } else {
                cached = remainsBefore.combineWith(transposedRemainsAfter);
            }
        } else if (removedRange.getEnd() <= cached.getStart()) {
            // Removal was before the cache. offset the cache.
            cached = cached.offsetBy(-removedRange.length());
        }

        getHandlers().forEach(dch -> dch.dataRemoved(firstRowIndex, count));

        ensureCoverageCheck();

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

        // Cache was not filled since previous insertRowData. The old rows are
        // no longer useful.
        if (invalidatedRows != null) {
            invalidatedRows.clear();
        }

        size += count;

        if (firstRowIndex <= cached.getStart()) {
            Range oldCached = cached;
            cached = cached.offsetBy(count);

            for (int i = 1; i <= cached.length(); i++) {
                int oldIndex = oldCached.getEnd() - i;
                int newIndex = cached.getEnd() - i;
                moveRowFromIndexToIndex(oldIndex, newIndex);
            }
        } else if (cached.contains(firstRowIndex)) {
            int oldCacheEnd = cached.getEnd();
            /*
             * We need to invalidate the cache from the inserted row onwards,
             * since the cache wants to be a contiguous range. It doesn't
             * support holes.
             *
             * If holes were supported, we could shift the higher part of
             * "cached" and leave a hole the size of "count" in the middle.
             */
            Range[] splitAt = cached.splitAt(firstRowIndex);
            cached = splitAt[0];
            Range invalid = splitAt[1];

            /*
             * If we already have a map in invalidatedRows, we're in a state
             * where multiple row manipulations without data received have
             * happened and the cache restoration is prevented completely.
             */

            if (!invalid.isEmpty() && invalidatedRows == null) {
                invalidatedRows = new HashMap<>();
                // Store all invalidated items to a map. Indices are updated to
                // match what they should be after the insertion.
                for (int i = invalid.getStart(); i < invalid.getEnd(); ++i) {
                    invalidatedRows.put(i + count, indexToRowMap.get(i));
                }
            }

            for (int i = firstRowIndex; i < oldCacheEnd; i++) {
                T row = indexToRowMap.remove(Integer.valueOf(i));
                keyToIndexMap.remove(getRowKey(row));
            }
        }

        getHandlers().forEach(dch -> dch.dataAdded(firstRowIndex, count));

        ensureCoverageCheck();

        Profiler.leave("AbstractRemoteDataSource.insertRowData");
    }

    @SuppressWarnings("boxing")
    private void moveRowFromIndexToIndex(int oldIndex, int newIndex) {
        T row = indexToRowMap.remove(oldIndex);
        if (indexToRowMap.containsKey(newIndex)) {
            // Old row is about to be overwritten. Remove it from keyCache.
            T row2 = indexToRowMap.remove(newIndex);
            if (row2 != null) {
                keyToIndexMap.remove(getRowKey(row2));
            }
        }
        indexToRowMap.put(newIndex, row);
        if (row != null) {
            keyToIndexMap.put(getRowKey(row), newIndex);
        }
    }

    /**
     * Gets the current range of cached rows.
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
        Range availableDataRange = getAvailableRangeForCache();

        Range minCacheRange = cacheStrategy.getMinCacheRange(
                requestedAvailability, cached, availableDataRange);

        assert minCacheRange.isSubsetOf(availableDataRange);

        return minCacheRange;
    }

    private Range getMaxCacheRange() {
        return getMaxCacheRange(getRequestedAvailability());
    }

    private Range getMaxCacheRange(Range range) {
        Range availableDataRange = getAvailableRangeForCache();
        Range maxCacheRange = cacheStrategy.getMaxCacheRange(range, cached,
                availableDataRange);

        assert maxCacheRange.isSubsetOf(availableDataRange);

        return maxCacheRange;
    }

    private Range getAvailableRangeForCache() {
        int upperBound = size();
        if (upperBound == -1) {
            upperBound = requestedAvailability.length();
        }
        return Range.withLength(0, upperBound);
    }

    @Override
    public RowHandle<T> getHandle(T row) throws IllegalStateException {
        Object key = getRowKey(row);

        if (key == null) {
            throw new NullPointerException(
                    "key may not be null (row: " + row + ")");
        }

        if (pinnedRows.containsKey(key)) {
            return pinnedRows.get(key);
        } else if (keyToIndexMap.containsKey(key)) {
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
    public abstract Object getRowKey(T row);

    @Override
    public int size() {
        return size;
    }

    /**
     * Updates the size, discarding all cached data. This method is used when
     * the size of the container is changed without any information about the
     * structure of the change. In this case, all cached data is discarded to
     * avoid cache offset issues.
     * <p>
     * If you have information about the structure of the change, use
     * {@link #insertRowData(int, int)} or {@link #removeRowData(int, int)} to
     * indicate where the inserted or removed rows are located.
     *
     * @param newSize
     *            the new size of the container
     */
    protected void resetDataAndSize(int newSize) {
        size = newSize;
        indexToRowMap.clear();
        keyToIndexMap.clear();
        cached = Range.withLength(0, 0);

        getHandlers().forEach(dch -> dch.resetDataAndSize(newSize));
    }

    protected int indexOfKey(Object rowKey) {
        if (!keyToIndexMap.containsKey(rowKey)) {
            return -1;
        } else {
            return keyToIndexMap.get(rowKey);
        }
    }

    protected boolean isPinned(T row) {
        return pinnedRows.containsKey(getRowKey(row));
    }

    /**
     * Checks if it is possible to currently fetch data from the remote data
     * source.
     *
     * @return <code>true</code> if it is ok to try to fetch data,
     *         <code>false</code> if it is known that fetching data will fail
     *         and should not be tried right now.
     * @since 7.7.2
     */
    protected boolean canFetchData() {
        return true;
    }
}
