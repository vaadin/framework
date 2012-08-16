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

import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

/**
 * FreeformStatementDelegate is an extension to FreeformQueryDelegate that
 * provides definitions for methods that produce StatementHelper objects instead
 * of basic query strings. This allows the FreeformQuery query delegate to use
 * PreparedStatements instead of regular Statement when accessing the database.
 * 
 * Due to the injection protection and other benefits of prepared statements, it
 * is advisable to implement this interface instead of the FreeformQueryDelegate
 * whenever possible.
 */
public interface FreeformStatementDelegate extends FreeformQueryDelegate {
    /**
     * Should return a new instance of StatementHelper that contains the query
     * string and parameter values required to create a PreparedStatement. This
     * method is responsible for gluing together the select query from the
     * filters and the order by conditions if these are supported.
     * 
     * @param offset
     *            the first record (row) to fetch.
     * @param pagelength
     *            the number of records (rows) to fetch. 0 means all records
     *            starting from offset.
     */
    public StatementHelper getQueryStatement(int offset, int limit)
            throws UnsupportedOperationException;

    /**
     * Should return a new instance of StatementHelper that contains the query
     * string and parameter values required to create a PreparedStatement that
     * will fetch the row count from the DB. Row count should be fetched using
     * filters that are currently set to the QueryDelegate.
     */
    public StatementHelper getCountStatement()
            throws UnsupportedOperationException;

    /**
     * Should return a new instance of StatementHelper that contains the query
     * string and parameter values required to create a PreparedStatement used
     * by the FreeformQuery.containsRowWithKeys() method. This is useful for
     * cases when the default logic in said method is not enough to support more
     * complex free form queries.
     * 
     * @param keys
     *            the values of the primary keys
     * @throws UnsupportedOperationException
     *             to use the default logic in FreeformQuery
     */
    public StatementHelper getContainsRowQueryStatement(Object... keys)
            throws UnsupportedOperationException;
}
