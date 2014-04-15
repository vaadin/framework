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
package com.vaadin.data.util.sqlcontainer.query.generator;

import java.io.Serializable;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

/**
 * The SQLGenerator interface is meant to be implemented for each different SQL
 * syntax that is to be supported. By default there are implementations for
 * HSQLDB, MySQL, PostgreSQL, MSSQL and Oracle syntaxes.
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
public interface SQLGenerator extends Serializable {
    /**
     * Generates a SELECT query with the provided parameters. Uses default
     * filtering mode (INCLUSIVE).
     * 
     * @param tableName
     *            Name of the table queried
     * @param filters
     *            The filters, converted into a WHERE clause
     * @param orderBys
     *            The the ordering conditions, converted into an ORDER BY clause
     * @param offset
     *            The offset of the first row to be included
     * @param pagelength
     *            The number of rows to be returned when the query executes
     * @param toSelect
     *            String containing what to select, e.g. "*", "COUNT(*)"
     * @return StatementHelper instance containing the query string for a
     *         PreparedStatement and the values required for the parameters
     */
    public StatementHelper generateSelectQuery(String tableName,
            List<Filter> filters, List<OrderBy> orderBys, int offset,
            int pagelength, String toSelect);

    /**
     * Generates an UPDATE query with the provided parameters.
     * 
     * @param tableName
     *            Name of the table queried
     * @param item
     *            RowItem containing the updated values update.
     * @return StatementHelper instance containing the query string for a
     *         PreparedStatement and the values required for the parameters
     */
    public StatementHelper generateUpdateQuery(String tableName, RowItem item);

    /**
     * Generates an INSERT query for inserting a new row with the provided
     * values.
     * 
     * @param tableName
     *            Name of the table queried
     * @param item
     *            New RowItem to be inserted into the database.
     * @return StatementHelper instance containing the query string for a
     *         PreparedStatement and the values required for the parameters
     */
    public StatementHelper generateInsertQuery(String tableName, RowItem item);

    /**
     * Generates a DELETE query for deleting data related to the given RowItem
     * from the database.
     * 
     * @param tableName
     *            Name of the table queried
     * @param primaryKeyColumns
     *            the names of the columns holding the primary key. Usually just
     *            one column, but might be several.
     * @param versionColumn
     *            the column containing the version number of the row, null if
     *            versioning (optimistic locking) not enabled.
     * @param item
     *            Item to be deleted from the database
     * @return StatementHelper instance containing the query string for a
     *         PreparedStatement and the values required for the parameters
     */
    public StatementHelper generateDeleteQuery(String tableName,
            List<String> primaryKeyColumns, String versionColumn, RowItem item);
}
