/*
 * Copyright 2011 Vaadin Ltd.
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
package com.vaadin.data.util.sqlcontainer.query;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;

public interface QueryDelegate extends Serializable {
    /**
     * Generates and executes a query to determine the current row count from
     * the DB. Row count will be fetched using filters that are currently set to
     * the QueryDelegate.
     * 
     * @return row count
     * @throws SQLException
     */
    public int getCount() throws SQLException;

    /**
     * Executes a paged SQL query and returns the ResultSet. The query is
     * defined through implementations of this QueryDelegate interface.
     * 
     * @param offset
     *            the first item of the page to load
     * @param pagelength
     *            the length of the page to load
     * @return a ResultSet containing the rows of the page
     * @throws SQLException
     *             if the database access fails.
     */
    public ResultSet getResults(int offset, int pagelength) throws SQLException;

    /**
     * Allows the SQLContainer implementation to check whether the QueryDelegate
     * implementation implements paging in the getResults method.
     * 
     * @see QueryDelegate#getResults(int, int)
     * 
     * @return true if the delegate implements paging
     */
    public boolean implementationRespectsPagingLimits();

    /**
     * Sets the filters to apply when performing the SQL query. These are
     * translated into a WHERE clause. Default filtering mode will be used.
     * 
     * @param filters
     *            The filters to apply.
     * @throws UnsupportedOperationException
     *             if the implementation doesn't support filtering.
     */
    public void setFilters(List<Filter> filters)
            throws UnsupportedOperationException;

    /**
     * Sets the order in which to retrieve rows from the database. The result
     * can be ordered by zero or more columns and each column can be in
     * ascending or descending order. These are translated into an ORDER BY
     * clause in the SQL query.
     * 
     * @param orderBys
     *            A list of the OrderBy conditions.
     * @throws UnsupportedOperationException
     *             if the implementation doesn't support ordering.
     */
    public void setOrderBy(List<OrderBy> orderBys)
            throws UnsupportedOperationException;

    /**
     * Stores a row in the database. The implementation of this interface
     * decides how to identify whether to store a new row or update an existing
     * one.
     * 
     * @param columnToValueMap
     *            A map containing the values for all columns to be stored or
     *            updated.
     * @return the number of affected rows in the database table
     * @throws UnsupportedOperationException
     *             if the implementation is read only.
     */
    public int storeRow(RowItem row) throws UnsupportedOperationException,
            SQLException;

    /**
     * Removes the given RowItem from the database.
     * 
     * @param row
     *            RowItem to be removed
     * @return true on success
     * @throws UnsupportedOperationException
     * @throws SQLException
     */
    public boolean removeRow(RowItem row) throws UnsupportedOperationException,
            SQLException;

    /**
     * Starts a new database transaction. Used when storing multiple changes.
     * 
     * Note that if a transaction is already open, it will be rolled back when a
     * new transaction is started.
     * 
     * @throws SQLException
     *             if the database access fails.
     */
    public void beginTransaction() throws SQLException;

    /**
     * Commits a transaction. If a transaction is not open nothing should
     * happen.
     * 
     * @throws SQLException
     *             if the database access fails.
     */
    public void commit() throws SQLException;

    /**
     * Rolls a transaction back. If a transaction is not open nothing should
     * happen.
     * 
     * @throws SQLException
     *             if the database access fails.
     */
    public void rollback() throws SQLException;

    /**
     * Returns a list of primary key column names. The list is either fetched
     * from the database (TableQuery) or given as an argument depending on
     * implementation.
     * 
     * @return
     */
    public List<String> getPrimaryKeyColumns();

    /**
     * Performs a query to find out whether the SQL table contains a row with
     * the given set of primary keys.
     * 
     * @param keys
     *            the primary keys
     * @return true if the SQL table contains a row with the provided keys
     * @throws SQLException
     */
    public boolean containsRowWithKey(Object... keys) throws SQLException;

    /************************/
    /** ROWID CHANGE EVENT **/
    /************************/

    /**
     * An <code>Event</code> object specifying the old and new RowId of an added
     * item after the addition has been successfully committed.
     */
    public interface RowIdChangeEvent extends Serializable {
        /**
         * Gets the old (temporary) RowId of the added row that raised this
         * event.
         * 
         * @return old RowId
         */
        public RowId getOldRowId();

        /**
         * Gets the new, possibly database assigned RowId of the added row that
         * raised this event.
         * 
         * @return new RowId
         */
        public RowId getNewRowId();
    }

    /** RowId change listener interface. */
    public interface RowIdChangeListener extends Serializable {
        /**
         * Lets the listener know that a RowId has been changed.
         * 
         * @param event
         */
        public void rowIdChange(QueryDelegate.RowIdChangeEvent event);
    }

    /**
     * The interface for adding and removing <code>RowIdChangeEvent</code>
     * listeners. By implementing this interface a class explicitly announces
     * that it will generate a <code>RowIdChangeEvent</code> when it performs a
     * database commit that may change the RowId.
     */
    public interface RowIdChangeNotifier extends Serializable {
        /**
         * Adds a RowIdChangeListener for the object.
         * 
         * @param listener
         *            listener to be added
         */
        public void addListener(QueryDelegate.RowIdChangeListener listener);

        /**
         * Removes the specified RowIdChangeListener from the object.
         * 
         * @param listener
         *            listener to be removed
         */
        public void removeListener(QueryDelegate.RowIdChangeListener listener);
    }
}
