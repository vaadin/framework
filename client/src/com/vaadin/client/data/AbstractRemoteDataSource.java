/*
 * Copyright 2000-2013 Vaadin Ltd.
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
import java.util.List;

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
 * @since 7.2
 * @author Vaadin Ltd
 * @param <T>
 *            the row type
 */
public abstract class AbstractRemoteDataSource<T> implements DataSource<T> {

    private boolean requestPending = false;

    private boolean coverageCheckPending = false;

    private Range requestedAvailability = Range.between(0, 0);

    private Range cached = Range.between(0, 0);

    private final HashMap<Integer, T> rowCache = new HashMap<Integer, T>();

    private DataChangeHandler dataChangeHandler;

    private int estimatedSize;

    private final ScheduledCommand coverageChecker = new ScheduledCommand() {
        @Override
        public void execute() {
            coverageCheckPending = false;
            checkCacheCoverage();
        }
    };

    /**
     * Sets the estimated number of rows in the data source.
     * 
     * @param estimatedSize
     *            the estimated number of available rows
     */
    protected void setEstimatedSize(int estimatedSize) {
        // TODO update dataChangeHandler if size changes
        this.estimatedSize = estimatedSize;
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
        if (requestPending) {
            // Anyone clearing requestPending should run this method again
            return;
        }

        Profiler.enter("AbstractRemoteDataSource.checkCacheCoverage");

        if (!requestedAvailability.intersects(cached) || cached.isEmpty()) {
            /*
             * Simple case: no overlap between cached data and needed data.
             * Clear the cache and request new data
             */
            rowCache.clear();
            cached = Range.between(0, 0);

            handleMissingRows(requestedAvailability);
        } else {
            discardStaleCacheEntries();

            // Might need more rows -> request them
            Range[] availabilityPartition = requestedAvailability
                    .partitionWith(cached);
            handleMissingRows(availabilityPartition[0]);
            handleMissingRows(availabilityPartition[2]);
        }

        Profiler.leave("AbstractRemoteDataSource.checkCacheCoverage");
    }

    private void discardStaleCacheEntries() {
        Range[] cacheParition = cached.partitionWith(requestedAvailability);
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
        requestPending = true;
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
        return estimatedSize;
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
        requestPending = false;

        Profiler.enter("AbstractRemoteDataSource.setRowData");

        Range received = Range.withLength(firstRowIndex, rowData.size());

        Range[] partition = received.partitionWith(requestedAvailability);

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
        if (removedRange.intersects(cached)) {
            Range[] partitions = cached.partitionWith(removedRange);
            Range remainsBefore = partitions[0];
            Range transposedRemainsAfter = partitions[2].offsetBy(-removedRange
                    .length());
            cached = remainsBefore.combineWith(transposedRemainsAfter);
        }
        estimatedSize -= count;
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

        estimatedSize += count;
        dataChangeHandler.dataAdded(firstRowIndex, count);
        checkCacheCoverage();

        Profiler.leave("AbstractRemoteDataSource.insertRowData");
    }
}
