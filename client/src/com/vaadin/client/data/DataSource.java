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

/**
 * Source of data for widgets showing lazily loaded data based on indexable
 * items (e.g. rows) of a specified type. The data source is a lazy view into a
 * larger data set.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @param <T>
 *            the row type
 */
public interface DataSource<T> {
    /**
     * Informs the data source that data for the given range is needed. A data
     * source only has one active region at a time, so calling this method
     * discards the previously set range.
     * <p>
     * This method triggers lazy loading of data if necessary. The change
     * handler registered using {@link #setDataChangeHandler(DataChangeHandler)}
     * is informed when new data has been loaded.
     * 
     * @param firstRowIndex
     *            the index of the first needed row
     * @param numberOfRows
     *            the number of needed rows
     */
    public void ensureAvailability(int firstRowIndex, int numberOfRows);

    /**
     * Retrieves the data for the row at the given index. If the row data is not
     * available, returns <code>null</code>.
     * <p>
     * This method does not trigger loading of unavailable data.
     * {@link #ensureAvailability(int, int)} should be used to signal what data
     * will be needed.
     * 
     * @param rowIndex
     *            the index of the row to retrieve data for
     * @return data for the row; or <code>null</code> if no data is available
     */
    public T getRow(int rowIndex);

    /**
     * Returns the current best guess for the number of rows in the container.
     * 
     * @return the current estimation of the container size
     */
    public int getEstimatedSize();

    /**
     * Sets a data change handler to inform when data is updated, added or
     * removed.
     * 
     * @param dataChangeHandler
     *            the data change handler
     */
    public void setDataChangeHandler(DataChangeHandler dataChangeHandler);

}
