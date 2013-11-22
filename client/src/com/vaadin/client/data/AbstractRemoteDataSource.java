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
import com.vaadin.client.ui.grid.Range;

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
                cached = cached.combineWith(newUsefulData);
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
}
