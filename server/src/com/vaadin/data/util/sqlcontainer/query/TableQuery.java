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

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.ColumnProperty;
import com.vaadin.data.util.sqlcontainer.OptimisticLockException;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.TemporaryRowId;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.MSSQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.SQLGenerator;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;

@SuppressWarnings("serial")
public class TableQuery extends AbstractTransactionalQuery implements
        QueryDelegate, QueryDelegate.RowIdChangeNotifier {

    /**
     * Table name (without catalog or schema information).
     */
    private String tableName;
    private String catalogName;
    private String schemaName;
    /**
     * Cached concatenated version of the table name.
     */
    private String fullTableName;
    /**
     * Primary key column name(s) in the table.
     */
    private List<String> primaryKeyColumns;
    /**
     * Version column name in the table.
     */
    private String versionColumn;

    /** Currently set Filters and OrderBys */
    private List<Filter> filters;
    private List<OrderBy> orderBys;

    /** SQLGenerator instance to use for generating queries */
    private SQLGenerator sqlGenerator;

    /** Row ID change listeners */
    private LinkedList<RowIdChangeListener> rowIdChangeListeners;
    /** Row ID change events, stored until commit() is called */
    private final List<RowIdChangeEvent> bufferedEvents = new ArrayList<RowIdChangeEvent>();

    /** Set to true to output generated SQL Queries to System.out */
    private final boolean debug = false;

    /**
     * Creates a new TableQuery using the given connection pool, SQL generator
     * and table name to fetch the data from. All parameters must be non-null.
     * 
     * The table name must be a simple name with no catalog or schema
     * information. If those are needed, use
     * {@link #TableQuery(String, String, String, JDBCConnectionPool, SQLGenerator)}
     * .
     * 
     * @param tableName
     *            Name of the database table to connect to
     * @param connectionPool
     *            Connection pool for accessing the database
     * @param sqlGenerator
     *            SQL query generator implementation
     */
    public TableQuery(String tableName, JDBCConnectionPool connectionPool,
            SQLGenerator sqlGenerator) {
        this(null, null, tableName, connectionPool, sqlGenerator);
    }

    /**
     * Creates a new TableQuery using the given connection pool, SQL generator
     * and table name to fetch the data from. Catalog and schema names can be
     * null, all other parameters must be non-null.
     * 
     * @param catalogName
     *            Name of the database catalog (can be null)
     * @param schemaName
     *            Name of the database schema (can be null)
     * @param tableName
     *            Name of the database table to connect to
     * @param connectionPool
     *            Connection pool for accessing the database
     * @param sqlGenerator
     *            SQL query generator implementation
     * @since 7.1
     */
    public TableQuery(String catalogName, String schemaName, String tableName,
            JDBCConnectionPool connectionPool, SQLGenerator sqlGenerator) {
        this(catalogName, schemaName, tableName, connectionPool, sqlGenerator,
                true);
    }

    /**
     * Creates a new TableQuery using the given connection pool and table name
     * to fetch the data from. All parameters must be non-null. The default SQL
     * generator will be used for queries.
     * 
     * The table name must be a simple name with no catalog or schema
     * information. If those are needed, use
     * {@link #TableQuery(String, String, String, JDBCConnectionPool, SQLGenerator)}
     * .
     * 
     * @param tableName
     *            Name of the database table to connect to
     * @param connectionPool
     *            Connection pool for accessing the database
     */
    public TableQuery(String tableName, JDBCConnectionPool connectionPool) {
        this(tableName, connectionPool, new DefaultSQLGenerator());
    }

    /**
     * Creates a new TableQuery using the given connection pool, SQL generator
     * and table name to fetch the data from. Catalog and schema names can be
     * null, all other parameters must be non-null.
     * 
     * @param catalogName
     *            Name of the database catalog (can be null)
     * @param schemaName
     *            Name of the database schema (can be null)
     * @param tableName
     *            Name of the database table to connect to
     * @param connectionPool
     *            Connection pool for accessing the database
     * @param sqlGenerator
     *            SQL query generator implementation
     * @param escapeNames
     *            true to escape special characters in catalog, schema and table
     *            names, false to use the names as-is
     * @since 7.1
     */
    protected TableQuery(String catalogName, String schemaName,
            String tableName, JDBCConnectionPool connectionPool,
            SQLGenerator sqlGenerator, boolean escapeNames) {
        super(connectionPool);
        if (tableName == null || tableName.trim().length() < 1
                || connectionPool == null || sqlGenerator == null) {
            throw new IllegalArgumentException(
                    "Table name, connection pool and SQL generator parameters must be non-null and non-empty.");
        }
        if (escapeNames) {
            this.catalogName = SQLUtil.escapeSQL(catalogName);
            this.schemaName = SQLUtil.escapeSQL(schemaName);
            this.tableName = SQLUtil.escapeSQL(tableName);
        } else {
            this.catalogName = catalogName;
            this.schemaName = schemaName;
            this.tableName = tableName;
        }
        this.sqlGenerator = sqlGenerator;
        fetchMetaData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#getCount()
     */
    @Override
    public int getCount() throws SQLException {
        getLogger().log(Level.FINE, "Fetching count...");
        StatementHelper sh = sqlGenerator.generateSelectQuery(
                getFullTableName(), filters, null, 0, 0, "COUNT(*)");
        boolean shouldCloseTransaction = false;
        if (!isInTransaction()) {
            shouldCloseTransaction = true;
            beginTransaction();
        }
        ResultSet r = null;
        int count = -1;
        try {
            r = executeQuery(sh);
            r.next();
            count = r.getInt(1);
        } finally {
            try {
                if (r != null) {
                    releaseConnection(r.getStatement().getConnection(),
                            r.getStatement(), r);
                }
            } finally {
                if (shouldCloseTransaction) {
                    commit();
                }
            }
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#getResults(int,
     * int)
     */
    @Override
    public ResultSet getResults(int offset, int pagelength) throws SQLException {
        StatementHelper sh;
        /*
         * If no ordering is explicitly set, results will be ordered by the
         * first primary key column.
         */
        if (orderBys == null || orderBys.isEmpty()) {
            List<OrderBy> ob = new ArrayList<OrderBy>();
            for (int i = 0; i < primaryKeyColumns.size(); i++) {
                ob.add(new OrderBy(primaryKeyColumns.get(i), true));
            }
            sh = sqlGenerator.generateSelectQuery(getFullTableName(), filters,
                    ob, offset, pagelength, null);
        } else {
            sh = sqlGenerator.generateSelectQuery(getFullTableName(), filters,
                    orderBys, offset, pagelength, null);
        }
        return executeQuery(sh);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#
     * implementationRespectsPagingLimits()
     */
    @Override
    public boolean implementationRespectsPagingLimits() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#storeRow(com.vaadin
     * .addon.sqlcontainer.RowItem)
     */
    @Override
    public int storeRow(RowItem row) throws UnsupportedOperationException,
            SQLException {
        if (row == null) {
            throw new IllegalArgumentException("Row argument must be non-null.");
        }
        StatementHelper sh;
        int result = 0;
        if (row.getId() instanceof TemporaryRowId) {
            setVersionColumnFlagInProperty(row);
            sh = sqlGenerator.generateInsertQuery(getFullTableName(), row);
            result = executeUpdateReturnKeys(sh, row);
        } else {
            setVersionColumnFlagInProperty(row);
            sh = sqlGenerator.generateUpdateQuery(getFullTableName(), row);
            result = executeUpdate(sh);
        }
        if (versionColumn != null && result == 0) {
            throw new OptimisticLockException(
                    "Someone else changed the row that was being updated.",
                    row.getId());
        }
        return result;
    }

    private void setVersionColumnFlagInProperty(RowItem row) {
        ColumnProperty versionProperty = (ColumnProperty) row
                .getItemProperty(versionColumn);
        if (versionProperty != null) {
            versionProperty.setVersionColumn(true);
        }
    }

    /**
     * Inserts the given row in the database table immediately. Begins and
     * commits the transaction needed. This method was added specifically to
     * solve the problem of returning the final RowId immediately on the
     * SQLContainer.addItem() call when auto commit mode is enabled in the
     * SQLContainer.
     * 
     * @param row
     *            RowItem to add to the database
     * @return Final RowId of the added row
     * @throws SQLException
     */
    public RowId storeRowImmediately(RowItem row) throws SQLException {
        beginTransaction();
        /* Set version column, if one is provided */
        setVersionColumnFlagInProperty(row);
        /* Generate query */
        StatementHelper sh = sqlGenerator.generateInsertQuery(
                getFullTableName(), row);
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        connection = getConnection();
        try {
            pstmt = connection.prepareStatement(sh.getQueryString(),
                    primaryKeyColumns.toArray(new String[0]));
            sh.setParameterValuesToStatement(pstmt);
            getLogger().log(Level.FINE, "DB -> {0}", sh.getQueryString());
            int result = pstmt.executeUpdate();
            RowId newId = null;
            if (result > 0) {
                /*
                 * If affected rows exist, we'll get the new RowId, commit the
                 * transaction and return the new RowId.
                 */
                generatedKeys = pstmt.getGeneratedKeys();
                newId = getNewRowId(row, generatedKeys);
            }
            // transaction has to be closed in any case
            commit();
            return newId;
        } finally {
            releaseConnection(connection, pstmt, generatedKeys);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#setFilters(java.util
     * .List)
     */
    @Override
    public void setFilters(List<Filter> filters)
            throws UnsupportedOperationException {
        if (filters == null) {
            this.filters = null;
            return;
        }
        this.filters = Collections.unmodifiableList(filters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#setOrderBy(java.util
     * .List)
     */
    @Override
    public void setOrderBy(List<OrderBy> orderBys)
            throws UnsupportedOperationException {
        if (orderBys == null) {
            this.orderBys = null;
            return;
        }
        this.orderBys = Collections.unmodifiableList(orderBys);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#beginTransaction()
     */
    @Override
    public void beginTransaction() throws UnsupportedOperationException,
            SQLException {
        getLogger().log(Level.FINE, "DB -> begin transaction");
        super.beginTransaction();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#commit()
     */
    @Override
    public void commit() throws UnsupportedOperationException, SQLException {
        getLogger().log(Level.FINE, "DB -> commit");
        super.commit();

        /* Handle firing row ID change events */
        RowIdChangeEvent[] unFiredEvents = bufferedEvents
                .toArray(new RowIdChangeEvent[] {});
        bufferedEvents.clear();
        if (rowIdChangeListeners != null && !rowIdChangeListeners.isEmpty()) {
            for (RowIdChangeListener r : rowIdChangeListeners) {
                for (RowIdChangeEvent e : unFiredEvents) {
                    r.rowIdChange(e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#rollback()
     */
    @Override
    public void rollback() throws UnsupportedOperationException, SQLException {
        getLogger().log(Level.FINE, "DB -> rollback");
        super.rollback();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#getPrimaryKeyColumns()
     */
    @Override
    public List<String> getPrimaryKeyColumns() {
        return Collections.unmodifiableList(primaryKeyColumns);
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(String column) {
        versionColumn = column;
    }

    /**
     * Returns the table name for the query without catalog and schema
     * information.
     * 
     * @return table name, not null
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Returns the catalog name for the query.
     * 
     * @return catalog name, can be null
     * @since 7.1
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * Returns the catalog name for the query.
     * 
     * @return catalog name, can be null
     * @since 7.1
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Returns the complete table name obtained by concatenation of the catalog
     * and schema names (if any) and the table name.
     * 
     * This method can be overridden if customization is needed.
     * 
     * @return table name in the form it should be used in query and update
     *         statements
     * @since 7.1
     */
    protected String getFullTableName() {
        if (fullTableName == null) {
            StringBuilder sb = new StringBuilder();
            if (catalogName != null) {
                sb.append(catalogName).append(".");
            }
            if (schemaName != null) {
                sb.append(schemaName).append(".");
            }
            sb.append(tableName);
            fullTableName = sb.toString();
        }
        return fullTableName;
    }

    public SQLGenerator getSqlGenerator() {
        return sqlGenerator;
    }

    /**
     * Executes the given query string using either the active connection if a
     * transaction is already open, or a new connection from this query's
     * connection pool.
     * 
     * @param sh
     *            an instance of StatementHelper, containing the query string
     *            and parameter values.
     * @return ResultSet of the query
     * @throws SQLException
     */
    private ResultSet executeQuery(StatementHelper sh) throws SQLException {
        ensureTransaction();
        Connection connection = getConnection();
        PreparedStatement pstmt = null;
        try {
            pstmt = connection.prepareStatement(sh.getQueryString());
            sh.setParameterValuesToStatement(pstmt);
            getLogger().log(Level.FINE, "DB -> {0}", sh.getQueryString());
            return pstmt.executeQuery();
        } catch (SQLException e) {
            releaseConnection(null, pstmt, null);
            throw e;
        }
    }

    /**
     * Executes the given update query string using either the active connection
     * if a transaction is already open, or a new connection from this query's
     * connection pool.
     * 
     * @param sh
     *            an instance of StatementHelper, containing the query string
     *            and parameter values.
     * @return Number of affected rows
     * @throws SQLException
     */
    private int executeUpdate(StatementHelper sh) throws SQLException {
        PreparedStatement pstmt = null;
        Connection connection = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sh.getQueryString());
            sh.setParameterValuesToStatement(pstmt);
            getLogger().log(Level.FINE, "DB -> {0}", sh.getQueryString());
            int retval = pstmt.executeUpdate();
            return retval;
        } finally {
            releaseConnection(connection, pstmt, null);
        }
    }

    /**
     * Executes the given update query string using either the active connection
     * if a transaction is already open, or a new connection from this query's
     * connection pool.
     * 
     * Additionally adds a new RowIdChangeEvent to the event buffer.
     * 
     * @param sh
     *            an instance of StatementHelper, containing the query string
     *            and parameter values.
     * @param row
     *            the row item to update
     * @return Number of affected rows
     * @throws SQLException
     */
    private int executeUpdateReturnKeys(StatementHelper sh, RowItem row)
            throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet genKeys = null;
        Connection connection = null;
        try {
            connection = getConnection();
            pstmt = connection.prepareStatement(sh.getQueryString(),
                    primaryKeyColumns.toArray(new String[0]));
            sh.setParameterValuesToStatement(pstmt);
            getLogger().log(Level.FINE, "DB -> {0}", sh.getQueryString());
            int result = pstmt.executeUpdate();
            genKeys = pstmt.getGeneratedKeys();
            RowId newId = getNewRowId(row, genKeys);
            bufferedEvents.add(new RowIdChangeEvent(row.getId(), newId));
            return result;
        } finally {
            releaseConnection(connection, pstmt, genKeys);
        }
    }

    /**
     * Fetches name(s) of primary key column(s) from DB metadata.
     * 
     * Also tries to get the escape string to be used in search strings.
     */
    private void fetchMetaData() {
        Connection connection = null;
        ResultSet rs = null;
        ResultSet tables = null;
        try {
            connection = getConnection();
            DatabaseMetaData dbmd = connection.getMetaData();
            if (dbmd != null) {
                tables = dbmd.getTables(catalogName, schemaName, tableName,
                        null);
                if (!tables.next()) {
                    String catalog = (catalogName != null) ? catalogName
                            .toUpperCase() : null;
                    String schema = (schemaName != null) ? schemaName
                            .toUpperCase() : null;
                    tables = dbmd.getTables(catalog, schema,
                            tableName.toUpperCase(), null);
                    if (!tables.next()) {
                        throw new IllegalArgumentException(
                                "Table with the name \""
                                        + getFullTableName()
                                        + "\" was not found. Check your database contents.");
                    } else {
                        catalogName = catalog;
                        schemaName = schema;
                        tableName = tableName.toUpperCase();
                    }
                }
                tables.close();
                rs = dbmd.getPrimaryKeys(catalogName, schemaName, tableName);
                List<String> names = new ArrayList<String>();
                while (rs.next()) {
                    names.add(rs.getString("COLUMN_NAME"));
                }
                rs.close();
                if (!names.isEmpty()) {
                    primaryKeyColumns = names;
                }
                if (primaryKeyColumns == null || primaryKeyColumns.isEmpty()) {
                    throw new IllegalArgumentException(
                            "Primary key constraints have not been defined for the table \""
                                    + getFullTableName()
                                    + "\". Use FreeFormQuery to access this table.");
                }
                for (String colName : primaryKeyColumns) {
                    if (colName.equalsIgnoreCase("rownum")) {
                        if (getSqlGenerator() instanceof MSSQLGenerator
                                || getSqlGenerator() instanceof MSSQLGenerator) {
                            throw new IllegalArgumentException(
                                    "When using Oracle or MSSQL, a primary key column"
                                            + " named \'rownum\' is not allowed!");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                releaseConnection(connection, null, rs);
            } catch (SQLException ignore) {
            } finally {
                try {
                    if (tables != null) {
                        tables.close();
                    }
                } catch (SQLException ignore) {
                }
            }
        }
    }

    private RowId getNewRowId(RowItem row, ResultSet genKeys) {
        try {
            /* Fetch primary key values and generate a map out of them. */
            Map<String, Object> values = new HashMap<String, Object>();
            ResultSetMetaData rsmd = genKeys.getMetaData();
            int colCount = rsmd.getColumnCount();
            if (genKeys.next()) {
                for (int i = 1; i <= colCount; i++) {
                    values.put(rsmd.getColumnName(i), genKeys.getObject(i));
                }
            }
            /* Generate new RowId */
            List<Object> newRowId = new ArrayList<Object>();
            if (values.size() == 1) {
                if (primaryKeyColumns.size() == 1) {
                    newRowId.add(values.get(values.keySet().iterator().next()));
                } else {
                    for (String s : primaryKeyColumns) {
                        if (!((ColumnProperty) row.getItemProperty(s))
                                .isReadOnlyChangeAllowed()) {
                            newRowId.add(values.get(values.keySet().iterator()
                                    .next()));
                        } else {
                            newRowId.add(values.get(s));
                        }
                    }
                }
            } else {
                for (String s : primaryKeyColumns) {
                    newRowId.add(values.get(s));
                }
            }
            return new RowId(newRowId.toArray());
        } catch (Exception e) {
            getLogger()
                    .log(Level.FINE,
                            "Failed to fetch key values on insert: {0}",
                            e.getMessage());
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#removeRow(com.vaadin
     * .addon.sqlcontainer.RowItem)
     */
    @Override
    public boolean removeRow(RowItem row) throws UnsupportedOperationException,
            SQLException {
        if (getLogger().isLoggable(Level.FINE)) {
            getLogger().log(Level.FINE, "Removing row with id: {0}",
                    row.getId().getId()[0]);
        }
        if (executeUpdate(sqlGenerator.generateDeleteQuery(getFullTableName(),
                primaryKeyColumns, versionColumn, row)) == 1) {
            return true;
        }
        if (versionColumn != null) {
            throw new OptimisticLockException(
                    "Someone else changed the row that was being deleted.",
                    row.getId());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#containsRowWithKey(
     * java.lang.Object[])
     */
    @Override
    public boolean containsRowWithKey(Object... keys) throws SQLException {
        ArrayList<Filter> filtersAndKeys = new ArrayList<Filter>();
        if (filters != null) {
            filtersAndKeys.addAll(filters);
        }
        int ix = 0;
        for (String colName : primaryKeyColumns) {
            filtersAndKeys.add(new Equal(colName, keys[ix]));
            ix++;
        }
        StatementHelper sh = sqlGenerator.generateSelectQuery(
                getFullTableName(), filtersAndKeys, orderBys, 0, 0, "*");

        boolean shouldCloseTransaction = false;
        if (!isInTransaction()) {
            shouldCloseTransaction = true;
            beginTransaction();
        }
        ResultSet rs = null;
        try {
            rs = executeQuery(sh);
            boolean contains = rs.next();
            return contains;
        } finally {
            try {
                if (rs != null) {
                    releaseConnection(rs.getStatement().getConnection(),
                            rs.getStatement(), rs);
                }
            } finally {
                if (shouldCloseTransaction) {
                    commit();
                }
            }
        }
    }

    /**
     * Custom writeObject to call rollback() if object is serialized.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        try {
            rollback();
        } catch (SQLException ignored) {
        }
        out.defaultWriteObject();
    }

    /**
     * Simple RowIdChangeEvent implementation.
     */
    public static class RowIdChangeEvent extends EventObject implements
            QueryDelegate.RowIdChangeEvent {
        private final RowId oldId;
        private final RowId newId;

        private RowIdChangeEvent(RowId oldId, RowId newId) {
            super(oldId);
            this.oldId = oldId;
            this.newId = newId;
        }

        @Override
        public RowId getNewRowId() {
            return newId;
        }

        @Override
        public RowId getOldRowId() {
            return oldId;
        }
    }

    /**
     * Adds RowIdChangeListener to this query
     */
    @Override
    public void addRowIdChangeListener(RowIdChangeListener listener) {
        if (rowIdChangeListeners == null) {
            rowIdChangeListeners = new LinkedList<QueryDelegate.RowIdChangeListener>();
        }
        rowIdChangeListeners.add(listener);
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #addRowIdChangeListener(com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener)}
     **/
    @Override
    @Deprecated
    public void addListener(RowIdChangeListener listener) {
        addRowIdChangeListener(listener);
    }

    /**
     * Removes the given RowIdChangeListener from this query
     */
    @Override
    public void removeRowIdChangeListener(RowIdChangeListener listener) {
        if (rowIdChangeListeners != null) {
            rowIdChangeListeners.remove(listener);
        }
    }

    /**
     * @deprecated As of 7.0, replaced by
     *             {@link #removeRowIdChangeListener(com.vaadin.data.util.sqlcontainer.query.QueryDelegate.RowIdChangeListener)}
     **/
    @Override
    @Deprecated
    public void removeListener(RowIdChangeListener listener) {
        removeRowIdChangeListener(listener);
    }

    private static final Logger getLogger() {
        return Logger.getLogger(TableQuery.class.getName());
    }
}
