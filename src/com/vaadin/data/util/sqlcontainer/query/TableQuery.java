/*
@VaadinApache2LicenseForJavaFiles@
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
public class TableQuery implements QueryDelegate,
        QueryDelegate.RowIdChangeNotifier {

    private static final Logger logger = Logger.getLogger(TableQuery.class
            .getName());

    /** Table name, primary key column name(s) and version column name */
    private String tableName;
    private List<String> primaryKeyColumns;
    private String versionColumn;

    /** Currently set Filters and OrderBys */
    private List<Filter> filters;
    private List<OrderBy> orderBys;

    /** SQLGenerator instance to use for generating queries */
    private SQLGenerator sqlGenerator;

    /** Fields related to Connection and Transaction handling */
    private JDBCConnectionPool connectionPool;
    private transient Connection activeConnection;
    private boolean transactionOpen;

    /** Row ID change listeners */
    private LinkedList<RowIdChangeListener> rowIdChangeListeners;
    /** Row ID change events, stored until commit() is called */
    private final List<RowIdChangeEvent> bufferedEvents = new ArrayList<RowIdChangeEvent>();

    /** Set to true to output generated SQL Queries to System.out */
    private boolean debug = false;

    /** Prevent no-parameters instantiation of TableQuery */
    @SuppressWarnings("unused")
    private TableQuery() {
    }

    /**
     * Creates a new TableQuery using the given connection pool, SQL generator
     * and table name to fetch the data from. All parameters must be non-null.
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
        if (tableName == null || tableName.trim().length() < 1
                || connectionPool == null || sqlGenerator == null) {
            throw new IllegalArgumentException(
                    "All parameters must be non-null and a table name must be given.");
        }
        this.tableName = tableName;
        this.sqlGenerator = sqlGenerator;
        this.connectionPool = connectionPool;
        fetchMetaData();
    }

    /**
     * Creates a new TableQuery using the given connection pool and table name
     * to fetch the data from. All parameters must be non-null. The default SQL
     * generator will be used for queries.
     * 
     * @param tableName
     *            Name of the database table to connect to
     * @param connectionPool
     *            Connection pool for accessing the database
     */
    public TableQuery(String tableName, JDBCConnectionPool connectionPool) {
        this(tableName, connectionPool, new DefaultSQLGenerator());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#getCount()
     */
    public int getCount() throws SQLException {
        logger.log(Level.FINE, "Fetching count...");
        StatementHelper sh = sqlGenerator.generateSelectQuery(tableName,
                filters, null, 0, 0, "COUNT(*)");
        boolean shouldCloseTransaction = false;
        if (!transactionOpen) {
            shouldCloseTransaction = true;
            beginTransaction();
        }
        ResultSet r = executeQuery(sh);
        r.next();
        int count = r.getInt(1);
        r.getStatement().close();
        r.close();
        if (shouldCloseTransaction) {
            commit();
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#getResults(int,
     * int)
     */
    public ResultSet getResults(int offset, int pagelength) throws SQLException {
        StatementHelper sh;
        /*
         * If no ordering is explicitly set, results will be ordered by the
         * first primary key column.
         */
        if (orderBys == null || orderBys.isEmpty()) {
            List<OrderBy> ob = new ArrayList<OrderBy>();
            ob.add(new OrderBy(primaryKeyColumns.get(0), true));
            sh = sqlGenerator.generateSelectQuery(tableName, filters, ob,
                    offset, pagelength, null);
        } else {
            sh = sqlGenerator.generateSelectQuery(tableName, filters, orderBys,
                    offset, pagelength, null);
        }
        return executeQuery(sh);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#
     * implementationRespectsPagingLimits()
     */
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
    public int storeRow(RowItem row) throws UnsupportedOperationException,
            SQLException {
        if (row == null) {
            throw new IllegalArgumentException("Row argument must be non-null.");
        }
        StatementHelper sh;
        int result = 0;
        if (row.getId() instanceof TemporaryRowId) {
            setVersionColumnFlagInProperty(row);
            sh = sqlGenerator.generateInsertQuery(tableName, row);
            result = executeUpdateReturnKeys(sh, row);
        } else {
            setVersionColumnFlagInProperty(row);
            sh = sqlGenerator.generateUpdateQuery(tableName, row);
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
        StatementHelper sh = sqlGenerator.generateInsertQuery(tableName, row);
        PreparedStatement pstmt = activeConnection.prepareStatement(
                sh.getQueryString(), primaryKeyColumns.toArray(new String[0]));
        sh.setParameterValuesToStatement(pstmt);
        logger.log(Level.FINE, "DB -> " + sh.getQueryString());
        int result = pstmt.executeUpdate();
        if (result > 0) {
            /*
             * If affected rows exist, we'll get the new RowId, commit the
             * transaction and return the new RowId.
             */
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            RowId newId = getNewRowId(row, generatedKeys);
            generatedKeys.close();
            pstmt.clearParameters();
            pstmt.close();
            commit();
            return newId;
        } else {
            pstmt.clearParameters();
            pstmt.close();
            /* On failure return null */
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#setFilters(java.util
     * .List)
     */
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
    public void beginTransaction() throws UnsupportedOperationException,
            SQLException {
        if (transactionOpen && activeConnection != null) {
            throw new IllegalStateException();
        }

        logger.log(Level.FINE, "DB -> begin transaction");
        activeConnection = connectionPool.reserveConnection();
        activeConnection.setAutoCommit(false);
        transactionOpen = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.addon.sqlcontainer.query.QueryDelegate#commit()
     */
    public void commit() throws UnsupportedOperationException, SQLException {
        if (transactionOpen && activeConnection != null) {
            logger.log(Level.FINE, "DB -> commit");
            activeConnection.commit();
            connectionPool.releaseConnection(activeConnection);
        } else {
            throw new SQLException("No active transaction");
        }
        transactionOpen = false;

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
    public void rollback() throws UnsupportedOperationException, SQLException {
        if (transactionOpen && activeConnection != null) {
            logger.log(Level.FINE, "DB -> rollback");
            activeConnection.rollback();
            connectionPool.releaseConnection(activeConnection);
        } else {
            throw new SQLException("No active transaction");
        }
        transactionOpen = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.sqlcontainer.query.QueryDelegate#getPrimaryKeyColumns()
     */
    public List<String> getPrimaryKeyColumns() {
        return Collections.unmodifiableList(primaryKeyColumns);
    }

    public String getVersionColumn() {
        return versionColumn;
    }

    public void setVersionColumn(String column) {
        versionColumn = column;
    }

    public String getTableName() {
        return tableName;
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
        Connection c = null;
        if (transactionOpen && activeConnection != null) {
            c = activeConnection;
        } else {
            throw new SQLException("No active transaction!");
        }
        PreparedStatement pstmt = c.prepareStatement(sh.getQueryString());
        sh.setParameterValuesToStatement(pstmt);
        logger.log(Level.FINE, "DB -> " + sh.getQueryString());
        return pstmt.executeQuery();
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
        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            if (transactionOpen && activeConnection != null) {
                c = activeConnection;
            } else {
                c = connectionPool.reserveConnection();
            }
            pstmt = c.prepareStatement(sh.getQueryString());
            sh.setParameterValuesToStatement(pstmt);
            logger.log(Level.FINE, "DB -> " + sh.getQueryString());
            int retval = pstmt.executeUpdate();
            return retval;
        } finally {
            if (pstmt != null) {
                pstmt.clearParameters();
                pstmt.close();
            }
            if (!transactionOpen) {
                connectionPool.releaseConnection(c);
            }
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
        Connection c = null;
        PreparedStatement pstmt = null;
        ResultSet genKeys = null;
        try {
            if (transactionOpen && activeConnection != null) {
                c = activeConnection;
            } else {
                c = connectionPool.reserveConnection();
            }
            pstmt = c.prepareStatement(sh.getQueryString(),
                    primaryKeyColumns.toArray(new String[0]));
            sh.setParameterValuesToStatement(pstmt);
            logger.log(Level.FINE, "DB -> " + sh.getQueryString());
            int result = pstmt.executeUpdate();
            genKeys = pstmt.getGeneratedKeys();
            RowId newId = getNewRowId(row, genKeys);
            bufferedEvents.add(new RowIdChangeEvent(row.getId(), newId));
            return result;
        } finally {
            if (genKeys != null) {
                genKeys.close();
            }
            if (pstmt != null) {
                pstmt.clearParameters();
                pstmt.close();
            }
            if (!transactionOpen) {
                connectionPool.releaseConnection(c);
            }
        }
    }

    /**
     * Fetches name(s) of primary key column(s) from DB metadata.
     * 
     * Also tries to get the escape string to be used in search strings.
     */
    private void fetchMetaData() {
        Connection c = null;
        try {
            c = connectionPool.reserveConnection();
            DatabaseMetaData dbmd = c.getMetaData();
            if (dbmd != null) {
                tableName = SQLUtil.escapeSQL(tableName);
                ResultSet tables = dbmd.getTables(null, null, tableName, null);
                if (!tables.next()) {
                    tables = dbmd.getTables(null, null,
                            tableName.toUpperCase(), null);
                    if (!tables.next()) {
                        throw new IllegalArgumentException(
                                "Table with the name \""
                                        + tableName
                                        + "\" was not found. Check your database contents.");
                    } else {
                        tableName = tableName.toUpperCase();
                    }
                }
                tables.close();
                ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName);
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
                                    + tableName
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
            connectionPool.releaseConnection(c);
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
            logger.log(Level.FINE,
                    "Failed to fetch key values on insert: " + e.getMessage());
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
    public boolean removeRow(RowItem row) throws UnsupportedOperationException,
            SQLException {
        logger.log(Level.FINE, "Removing row with id: "
                + row.getId().getId()[0].toString());
        if (executeUpdate(sqlGenerator.generateDeleteQuery(getTableName(),
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
        StatementHelper sh = sqlGenerator.generateSelectQuery(tableName,
                filtersAndKeys, orderBys, 0, 0, "*");

        boolean shouldCloseTransaction = false;
        if (!transactionOpen) {
            shouldCloseTransaction = true;
            beginTransaction();
        }
        ResultSet rs = null;
        try {
            rs = executeQuery(sh);
            boolean contains = rs.next();
            return contains;
        } finally {
            if (rs != null) {
                if (rs.getStatement() != null) {
                    rs.getStatement().close();
                }
                rs.close();
            }
            if (shouldCloseTransaction) {
                commit();
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
    public class RowIdChangeEvent extends EventObject implements
            QueryDelegate.RowIdChangeEvent {
        private final RowId oldId;
        private final RowId newId;

        private RowIdChangeEvent(RowId oldId, RowId newId) {
            super(oldId);
            this.oldId = oldId;
            this.newId = newId;
        }

        public RowId getNewRowId() {
            return newId;
        }

        public RowId getOldRowId() {
            return oldId;
        }
    }

    /**
     * Adds RowIdChangeListener to this query
     */
    public void addListener(RowIdChangeListener listener) {
        if (rowIdChangeListeners == null) {
            rowIdChangeListeners = new LinkedList<QueryDelegate.RowIdChangeListener>();
        }
        rowIdChangeListeners.add(listener);
    }

    /**
     * Removes the given RowIdChangeListener from this query
     */
    public void removeListener(RowIdChangeListener listener) {
        if (rowIdChangeListeners != null) {
            rowIdChangeListeners.remove(listener);
        }
    }
}
