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

import com.vaadin.shared.ui.grid.Range;

/**
 * Determines what data an {@link AbstractRemoteDataSource} should fetch and
 * keep cached.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface CacheStrategy {
    /**
     * A helper class for creating a simple symmetric cache strategy that uses
     * the same logic for both rows before and after the currently cached range.
     * <p>
     * This simple approach rules out more advanced heuristics that would take
     * the current scrolling direction or past scrolling behavior into account.
     */
    public static abstract class AbstractBasicSymmetricalCacheStrategy
            implements CacheStrategy {

        @Override
        public void onDataArrive(double roundTripTime, int rowCount) {
            // NOP
        }

        @Override
        public Range getMinCacheRange(Range displayedRange, Range cachedRange,
                Range estimatedAvailableRange) {
            int cacheSize = getMinimumCacheSize(displayedRange.length());

            return displayedRange.expand(cacheSize, cacheSize).restrictTo(
                    estimatedAvailableRange);
        }

        @Override
        public Range getMaxCacheRange(Range displayedRange, Range cachedRange,
                Range estimatedAvailableRange) {
            int cacheSize = getMaximumCacheSize(displayedRange.length());

            return displayedRange.expand(cacheSize, cacheSize).restrictTo(
                    estimatedAvailableRange);
        }

        /**
         * Gets the maximum number of extra items to cache in one direction.
         * 
         * @param pageSize
         *            the current number of items used at once
         * @return maximum of items to cache
         */
        public abstract int getMaximumCacheSize(int pageSize);

        /**
         * Gets the the minimum number of extra items to cache in one direction.
         * 
         * @param pageSize
         *            the current number of items used at once
         * @return minimum number of items to cache
         */
        public abstract int getMinimumCacheSize(int pageSize);
    }

    /**
     * The default cache strategy used by {@link AbstractRemoteDataSource},
     * using multiples of the page size for determining the minimum and maximum
     * number of items to keep in the cache. By default, at least three times
     * the page size both before and after the currently used range are kept in
     * the cache and items are discarded if there's yet another page size worth
     * of items cached in either direction.
     */
    public static class DefaultCacheStrategy extends
            AbstractBasicSymmetricalCacheStrategy {
        private final int minimumRatio;
        private final int maximumRatio;

        /**
         * Creates a DefaultCacheStrategy keeping between 3 and 4 pages worth of
         * data cached both before and after the active range.
         */
        public DefaultCacheStrategy() {
            this(3, 4);
        }

        /**
         * Creates a DefaultCacheStrategy with custom ratios for how much data
         * to cache. The ratios denote how many multiples of the currently used
         * page size are kept in the cache in each direction.
         * 
         * @param minimumRatio
         *            the minimum number of pages to keep in the cache in each
         *            direction
         * @param maximumRatio
         *            the maximum number of pages to keep in the cache in each
         *            direction
         */
        public DefaultCacheStrategy(int minimumRatio, int maximumRatio) {
            this.minimumRatio = minimumRatio;
            this.maximumRatio = maximumRatio;
        }

        @Override
        public int getMinimumCacheSize(int pageSize) {
            return pageSize * minimumRatio;
        }

        @Override
        public int getMaximumCacheSize(int pageSize) {
            return pageSize * maximumRatio;
        }
    }

    /**
     * Called whenever data requested by the data source has arrived. This
     * information can e.g. be used for measuring how long it takes to fetch
     * different number of rows from the server.
     * <p>
     * A cache strategy implementation cannot use this information to keep track
     * of which items are in the cache since the data source might discard items
     * without notifying the cache strategy.
     * 
     * @param roundTripTime
     *            the total number of milliseconds elapsed from requesting the
     *            data until the response was passed to the data source
     * @param rowCount
     *            the number of received rows
     */
    public void onDataArrive(double roundTripTime, int rowCount);

    /**
     * Gets the minimum row range that should be cached. The data source will
     * fetch new data if the currently cached range does not fill the entire
     * minimum cache range.
     * 
     * @param displayedRange
     *            the range of currently displayed rows
     * @param cachedRange
     *            the range of currently cached rows
     * @param estimatedAvailableRange
     *            the estimated range of rows available for the data source
     * 
     * @return the minimum range of rows that should be cached, should at least
     *         include the displayed range and should not exceed the total
     *         estimated available range
     */
    public Range getMinCacheRange(Range displayedRange, Range cachedRange,
            Range estimatedAvailableRange);

    /**
     * Gets the maximum row range that should be cached. The data source will
     * discard cached rows that are outside the maximum range.
     * 
     * @param displayedRange
     *            the range of currently displayed rows
     * @param cachedRange
     *            the range of currently cached rows
     * @param estimatedAvailableRange
     *            the estimated range of rows available for the data source
     * 
     * @return the maximum range of rows that should be cached, should at least
     *         include the displayed range and should not exceed the total
     *         estimated available range
     */
    public Range getMaxCacheRange(Range displayedRange, Range cachedRange,
            Range estimatedAvailableRange);
}
