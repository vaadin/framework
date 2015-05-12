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
     * A handle that contains information on whether a row should be
     * {@link #pin() pinned} or {@link #unpin() unpinned}, and also always the
     * most recent representation for that particular row.
     * 
     * @param <T>
     *            the row type
     */
    public abstract class RowHandle<T> {
        /**
         * Gets the most recent representation for the row this handle
         * represents.
         * 
         * @return the most recent representation for the row this handle
         *         represents
         * @throws IllegalStateException
         *             if this row handle isn't currently pinned
         * @see #pin()
         */
        public abstract T getRow() throws IllegalStateException;

        /**
         * Marks this row as pinned.
         * <p>
         * <em>Note:</em> Pinning a row multiple times requires an equal amount
         * of unpins to free the row from the "pinned" status.
         * <p>
         * <em>Technical Note:</em> Pinning a row makes sure that the row object
         * for a particular set of data is always kept as up to date as the data
         * source is able to. Since the DataSource might create a new instance
         * of an object, object references aren't necessarily kept up-to-date.
         * This is a technical work-around for that.
         * 
         * @see #unpin()
         */
        public abstract void pin();

        /**
         * Marks this row as unpinned.
         * <p>
         * <em>Note:</em> Pinning a row multiple times requires an equal amount
         * of unpins to free the row from the "pinned" status.
         * <p>
         * <em>Technical Note:</em> Pinning a row makes sure that the row object
         * for a particular set of data is always kept as up to date as the data
         * source is able to. Since the DataSource might create a new instance
         * of an object, object references aren't necessarily kept up-to-date.
         * This is a technical work-around for that.
         * 
         * @throws IllegalStateException
         *             if this row handle has not been pinned before
         * @see #pin()
         */
        public abstract void unpin() throws IllegalStateException;

        /**
         * Informs the DataSource that the row data represented by this
         * RowHandle has been updated. DataChangeHandler for the DataSource
         * should be informed that parts of data have been updated.
         * 
         * @see DataChangeHandler#dataUpdated(int, int)
         */
        public abstract void updateRow();

        /**
         * An explicit override for {@link Object#equals(Object)}. This method
         * should be functionally equivalent to a properly implemented equals
         * method.
         * <p>
         * Having a properly implemented equals method is imperative for
         * RowHandle to function. Because Java has no mechanism to force an
         * override of an existing method, we're defining a new method for that
         * instead.
         * 
         * @param rowHandle
         *            the reference object with which to compare
         * @return {@code true} if this object is the same as the obj argument;
         *         {@code false} otherwise.
         */
        protected abstract boolean equalsExplicit(Object obj);

        /**
         * An explicit override for {@link Object#hashCode()}. This method
         * should be functionally equivalent to a properly implemented hashCode
         * method.
         * <p>
         * Having a properly implemented hashCode method is imperative for
         * RowHandle to function. Because Java has no mechanism to force an
         * override of an existing method, we're defining a new method for that
         * instead.
         * 
         * @return a hash code value for this object
         */
        protected abstract int hashCodeExplicit();

        @Override
        public int hashCode() {
            return hashCodeExplicit();
        }

        @Override
        public boolean equals(Object obj) {
            return equalsExplicit(obj);
        }
    }

    /**
     * Informs the data source that data for the given range is needed. A data
     * source only has one active region at a time, so calling this method
     * discards the previously set range.
     * <p>
     * This method triggers lazy loading of data if necessary. The change
     * handler registered using {@link #setDataChangeHandler(DataChangeHandler)}
     * is informed when new data has been loaded.
     * <p>
     * After any possible lazy loading and updates are done, the change handler
     * is informed that new data is available.
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
     * Returns the number of rows in the data source.
     * 
     * @return the current size of the data source
     */
    public int size();

    /**
     * Sets a data change handler to inform when data is updated, added or
     * removed.
     * 
     * @param dataChangeHandler
     *            the data change handler
     */
    public void setDataChangeHandler(DataChangeHandler dataChangeHandler);

    /**
     * Gets a {@link RowHandle} of a row object in the cache.
     * 
     * @param row
     *            the row object for which to retrieve a row handle
     * @return a non-<code>null</code> row handle of the given row object
     * @throw IllegalStateException if this data source cannot be sure whether
     *        or not the given row exists. <em>In practice</em> this usually
     *        means that the row is not currently in this data source's cache.
     */
    public RowHandle<T> getHandle(T row);
}
