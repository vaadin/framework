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
package com.vaadin.data.util.sqlcontainer.query;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;

public interface FreeformQueryDelegate extends Serializable {
    /**
     * Should return the SQL query string to be performed. This method is
     * responsible for gluing together the select query from the filters and the
     * order by conditions if these are supported.
     * 
     * @param offset
     *            the first record (row) to fetch.
     * @param pagelength
     *            the number of records (rows) to fetch. 0 means all records
     *            starting from offset.
     * @deprecated As of 6.7. Implement {@link FreeformStatementDelegate}
     *             instead of {@link FreeformQueryDelegate}
     */
    @Deprecated
    public String getQueryString(int offset, int limit)
            throws UnsupportedOperationException;

    /**
     * Generates and executes a query to determine the current row count from
     * the DB. Row count will be fetched using filters that are currently set to
     * the QueryDelegate.
     * 
     * @return row count
     * @throws SQLException
     * @deprecated As of 6.7. Implement {@link FreeformStatementDelegate}
     *             instead of {@link FreeformQueryDelegate}
     */
    @Deprecated
    public String getCountQuery() throws UnsupportedOperationException;

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
     * @param conn
     *            the JDBC connection to use
     * @param row
     *            RowItem to be stored or updated.
     * @throws UnsupportedOperationException
     *             if the implementation is read only.
     * @throws SQLException
     */
    public int storeRow(Connection conn, RowItem row)
            throws UnsupportedOperationException, SQLException;

    /**
     * Removes the given RowItem from the database.
     * 
     * @param conn
     *            the JDBC connection to use
     * @param row
     *            RowItem to be removed
     * @return true on success
     * @throws UnsupportedOperationException
     * @throws SQLException
     */
    public boolean removeRow(Connection conn, RowItem row)
            throws UnsupportedOperationException, SQLException;

    /**
     * Generates an SQL Query string that allows the user of the FreeformQuery
     * class to customize the query string used by the
     * FreeformQuery.containsRowWithKeys() method. This is useful for cases when
     * the logic in the containsRowWithKeys method is not enough to support more
     * complex free form queries.
     * 
     * @param keys
     *            the values of the primary keys
     * @throws UnsupportedOperationException
     *             to use the default logic in FreeformQuery
     * @deprecated As of 6.7. Implement {@link FreeformStatementDelegate}
     *             instead of {@link FreeformQueryDelegate}
     */
    @Deprecated
    public String getContainsRowQueryString(Object... keys)
            throws UnsupportedOperationException;
}
