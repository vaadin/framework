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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.ColumnProperty;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.StringDecorator;

/**
 * Generates generic SQL that is supported by HSQLDB, MySQL and PostgreSQL.
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
@SuppressWarnings("serial")
public class DefaultSQLGenerator implements SQLGenerator {

    private Class<? extends StatementHelper> statementHelperClass = null;

    public DefaultSQLGenerator() {

    }

    /**
     * Create a new DefaultSqlGenerator instance that uses the given
     * implementation of {@link StatementHelper}
     * 
     * @param statementHelper
     */
    public DefaultSQLGenerator(
            Class<? extends StatementHelper> statementHelperClazz) {
        this();
        statementHelperClass = statementHelperClazz;
    }

    /**
     * Construct a DefaultSQLGenerator with the specified identifiers for start
     * and end of quoted strings. The identifiers may be different depending on
     * the database engine and it's settings.
     * 
     * @param quoteStart
     *            the identifier (character) denoting the start of a quoted
     *            string
     * @param quoteEnd
     *            the identifier (character) denoting the end of a quoted string
     */
    public DefaultSQLGenerator(String quoteStart, String quoteEnd) {
        QueryBuilder.setStringDecorator(new StringDecorator(quoteStart,
                quoteEnd));
    }

    /**
     * Same as {@link #DefaultSQLGenerator(String, String)} but with support for
     * custom {@link StatementHelper} implementation.
     * 
     * @param quoteStart
     * @param quoteEnd
     * @param statementHelperClazz
     */
    public DefaultSQLGenerator(String quoteStart, String quoteEnd,
            Class<? extends StatementHelper> statementHelperClazz) {
        this(quoteStart, quoteEnd);
        statementHelperClass = statementHelperClazz;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.generator.SQLGenerator#
     * generateSelectQuery(java.lang.String, java.util.List, java.util.List,
     * int, int, java.lang.String)
     */
    @Override
    public StatementHelper generateSelectQuery(String tableName,
            List<Filter> filters, List<OrderBy> orderBys, int offset,
            int pagelength, String toSelect) {
        if (tableName == null || tableName.trim().equals("")) {
            throw new IllegalArgumentException("Table name must be given.");
        }
        toSelect = toSelect == null ? "*" : toSelect;
        StatementHelper sh = getStatementHelper();
        StringBuffer query = new StringBuffer();
        query.append("SELECT " + toSelect + " FROM ").append(
                SQLUtil.escapeSQL(tableName));
        if (filters != null) {
            query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
        }
        if (orderBys != null) {
            for (OrderBy o : orderBys) {
                generateOrderBy(query, o, orderBys.indexOf(o) == 0);
            }
        }
        if (pagelength != 0) {
            generateLimits(query, offset, pagelength);
        }
        sh.setQueryString(query.toString());
        return sh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.generator.SQLGenerator#
     * generateUpdateQuery(java.lang.String,
     * com.vaadin.addon.sqlcontainer.RowItem)
     */
    @Override
    public StatementHelper generateUpdateQuery(String tableName, RowItem item) {
        if (tableName == null || tableName.trim().equals("")) {
            throw new IllegalArgumentException("Table name must be given.");
        }
        if (item == null) {
            throw new IllegalArgumentException("Updated item must be given.");
        }
        StatementHelper sh = getStatementHelper();
        StringBuffer query = new StringBuffer();
        query.append("UPDATE ").append(tableName).append(" SET");

        /* Generate column<->value and rowidentifiers map */
        Map<String, Object> columnToValueMap = generateColumnToValueMap(item);
        Map<String, Object> rowIdentifiers = generateRowIdentifiers(item);
        /* Generate columns and values to update */
        boolean first = true;
        for (String column : columnToValueMap.keySet()) {
            if (first) {
                query.append(" " + QueryBuilder.quote(column) + " = ?");
            } else {
                query.append(", " + QueryBuilder.quote(column) + " = ?");
            }
            sh.addParameterValue(columnToValueMap.get(column), item
                    .getItemProperty(column).getType());
            first = false;
        }
        /* Generate identifiers for the row to be updated */
        first = true;
        for (String column : rowIdentifiers.keySet()) {
            if (first) {
                query.append(" WHERE " + QueryBuilder.quote(column) + " = ?");
            } else {
                query.append(" AND " + QueryBuilder.quote(column) + " = ?");
            }
            sh.addParameterValue(rowIdentifiers.get(column), item
                    .getItemProperty(column).getType());
            first = false;
        }
        sh.setQueryString(query.toString());
        return sh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.generator.SQLGenerator#
     * generateInsertQuery(java.lang.String,
     * com.vaadin.addon.sqlcontainer.RowItem)
     */
    @Override
    public StatementHelper generateInsertQuery(String tableName, RowItem item) {
        if (tableName == null || tableName.trim().equals("")) {
            throw new IllegalArgumentException("Table name must be given.");
        }
        if (item == null) {
            throw new IllegalArgumentException("New item must be given.");
        }
        if (!(item.getId() instanceof TemporaryRowId)) {
            throw new IllegalArgumentException(
                    "Cannot generate an insert query for item already in database.");
        }
        StatementHelper sh = getStatementHelper();
        StringBuffer query = new StringBuffer();
        query.append("INSERT INTO ").append(tableName).append(" (");

        /* Generate column<->value map */
        Map<String, Object> columnToValueMap = generateColumnToValueMap(item);
        /* Generate column names for insert query */
        boolean first = true;
        for (String column : columnToValueMap.keySet()) {
            if (!first) {
                query.append(", ");
            }
            query.append(QueryBuilder.quote(column));
            first = false;
        }

        /* Generate values for insert query */
        query.append(") VALUES (");
        first = true;
        for (String column : columnToValueMap.keySet()) {
            if (!first) {
                query.append(", ");
            }
            query.append("?");
            sh.addParameterValue(columnToValueMap.get(column), item
                    .getItemProperty(column).getType());
            first = false;
        }
        query.append(")");
        sh.setQueryString(query.toString());
        return sh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.generator.SQLGenerator#
     * generateDeleteQuery(java.lang.String,
     * com.vaadin.addon.sqlcontainer.RowItem)
     */
    @Override
    public StatementHelper generateDeleteQuery(String tableName,
            List<String> primaryKeyColumns, String versionColumn, RowItem item) {
        if (tableName == null || tableName.trim().equals("")) {
            throw new IllegalArgumentException("Table name must be given.");
        }
        if (item == null) {
            throw new IllegalArgumentException(
                    "Item to be deleted must be given.");
        }
        if (primaryKeyColumns == null || primaryKeyColumns.isEmpty()) {
            throw new IllegalArgumentException(
                    "Valid keyColumnNames must be provided.");
        }
        StatementHelper sh = getStatementHelper();
        StringBuffer query = new StringBuffer();
        query.append("DELETE FROM ").append(tableName).append(" WHERE ");
        int count = 1;
        for (String keyColName : primaryKeyColumns) {
            if ((this instanceof MSSQLGenerator || this instanceof OracleGenerator)
                    && keyColName.equalsIgnoreCase("rownum")) {
                count++;
                continue;
            }
            if (count > 1) {
                query.append(" AND ");
            }
            if (item.getItemProperty(keyColName).getValue() != null) {
                query.append(QueryBuilder.quote(keyColName) + " = ?");
                sh.addParameterValue(item.getItemProperty(keyColName)
                        .getValue(), item.getItemProperty(keyColName).getType());
            }
            count++;
        }
        if (versionColumn != null) {
            query.append(String.format(" AND %s = ?",
                    QueryBuilder.quote(versionColumn)));
            sh.addParameterValue(
                    item.getItemProperty(versionColumn).getValue(), item
                            .getItemProperty(versionColumn).getType());
        }

        sh.setQueryString(query.toString());
        return sh;
    }

    /**
     * Generates sorting rules as an ORDER BY -clause
     * 
     * @param sb
     *            StringBuffer to which the clause is appended.
     * @param o
     *            OrderBy object to be added into the sb.
     * @param firstOrderBy
     *            If true, this is the first OrderBy.
     * @return
     */
    protected StringBuffer generateOrderBy(StringBuffer sb, OrderBy o,
            boolean firstOrderBy) {
        if (firstOrderBy) {
            sb.append(" ORDER BY ");
        } else {
            sb.append(", ");
        }
        sb.append(QueryBuilder.quote(o.getColumn()));
        if (o.isAscending()) {
            sb.append(" ASC");
        } else {
            sb.append(" DESC");
        }
        return sb;
    }

    /**
     * Generates the LIMIT and OFFSET clause.
     * 
     * @param sb
     *            StringBuffer to which the clause is appended.
     * @param offset
     *            Value for offset.
     * @param pagelength
     *            Value for pagelength.
     * @return StringBuffer with LIMIT and OFFSET clause added.
     */
    protected StringBuffer generateLimits(StringBuffer sb, int offset,
            int pagelength) {
        sb.append(" LIMIT ").append(pagelength).append(" OFFSET ")
                .append(offset);
        return sb;
    }

    protected Map<String, Object> generateColumnToValueMap(RowItem item) {
        Map<String, Object> columnToValueMap = new HashMap<String, Object>();
        for (Object id : item.getItemPropertyIds()) {
            ColumnProperty cp = (ColumnProperty) item.getItemProperty(id);
            /* Prevent "rownum" usage as a column name if MSSQL or ORACLE */
            if ((this instanceof MSSQLGenerator || this instanceof OracleGenerator)
                    && cp.getPropertyId().equalsIgnoreCase("rownum")) {
                continue;
            }
            if (cp.isPersistent()) {
                columnToValueMap.put(cp.getPropertyId(), cp.getValue());
            }
        }
        return columnToValueMap;
    }

    protected Map<String, Object> generateRowIdentifiers(RowItem item) {
        Map<String, Object> rowIdentifiers = new HashMap<String, Object>();
        for (Object id : item.getItemPropertyIds()) {
            ColumnProperty cp = (ColumnProperty) item.getItemProperty(id);
            /* Prevent "rownum" usage as a column name if MSSQL or ORACLE */
            if ((this instanceof MSSQLGenerator || this instanceof OracleGenerator)
                    && cp.getPropertyId().equalsIgnoreCase("rownum")) {
                continue;
            }

            if (cp.isRowIdentifier()) {
                Object value;
                if (cp.isPrimaryKey()) {
                    // If the value of a primary key has changed, its old value
                    // should be used to identify the row (#9145)
                    value = cp.getOldValue();
                } else {
                    value = cp.getValue();
                }
                rowIdentifiers.put(cp.getPropertyId(), value);
            }
        }
        return rowIdentifiers;
    }

    /**
     * Returns the statement helper for the generator. Override this to handle
     * platform specific data types.
     * 
     * @see http://dev.vaadin.com/ticket/9148
     * @return a new instance of the statement helper
     */
    protected StatementHelper getStatementHelper() {
        if (statementHelperClass == null) {
            return new StatementHelper();
        }

        try {
            return statementHelperClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(
                    "Unable to instantiate custom StatementHelper", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                    "Unable to instantiate custom StatementHelper", e);
        }
    }

}
