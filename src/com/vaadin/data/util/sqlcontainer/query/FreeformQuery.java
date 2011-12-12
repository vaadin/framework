/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer.query;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

@SuppressWarnings("serial")
public class FreeformQuery implements QueryDelegate {

    FreeformQueryDelegate delegate = null;
    private String queryString;
    private List<String> primaryKeyColumns;
    private JDBCConnectionPool connectionPool;
    private transient Connection activeConnection = null;

    /**
     * Prevent no-parameters instantiation of FreeformQuery
     */
    @SuppressWarnings("unused")
    private FreeformQuery() {
    }

    /**
     * Creates a new freeform query delegate to be used with the
     * {@link SQLContainer}.
     * 
     * @param queryString
     *            The actual query to perform.
     * @param primaryKeyColumns
     *            The primary key columns. Read-only mode is forced if this
     *            parameter is null or empty.
     * @param connectionPool
     *            the JDBCConnectionPool to use to open connections to the SQL
     *            database.
     * @deprecated @see
     *             {@link FreeformQuery#FreeformQuery(String, JDBCConnectionPool, String...)}
     */
    @Deprecated
    public FreeformQuery(String queryString, List<String> primaryKeyColumns,
            JDBCConnectionPool connectionPool) {
        if (primaryKeyColumns == null) {
            primaryKeyColumns = new ArrayList<String>();
        }
        if (primaryKeyColumns.contains("")) {
            throw new IllegalArgumentException(
                    "The primary key columns contain an empty string!");
        } else if (queryString == null || "".equals(queryString)) {
            throw new IllegalArgumentException(
                    "The query string may not be empty or null!");
        } else if (connectionPool == null) {
            throw new IllegalArgumentException(
                    "The connectionPool may not be null!");
        }
        this.queryString = queryString;
        this.primaryKeyColumns = Collections
                .unmodifiableList(primaryKeyColumns);
        this.connectionPool = connectionPool;
    }

    /**
     * Creates a new freeform query delegate to be used with the
     * {@link SQLContainer}.
     * 
     * @param queryString
     *            The actual query to perform.
     * @param connectionPool
     *            the JDBCConnectionPool to use to open connections to the SQL
     *            database.
     * @param primaryKeyColumns
     *            The primary key columns. Read-only mode is forced if none are
     *            provided. (optional)
     */
    public FreeformQuery(String queryString, JDBCConnectionPool connectionPool,
            String... primaryKeyColumns) {
        this(queryString, Arrays.asList(primaryKeyColumns), connectionPool);
    }

    /**
     * This implementation of getCount() actually fetches all records from the
     * database, which might be a performance issue. Override this method with a
     * SELECT COUNT(*) ... query if this is too slow for your needs.
     * 
     * {@inheritDoc}
     */
    public int getCount() throws SQLException {
        // First try the delegate
        int count = countByDelegate();
        if (count < 0) {
            // Couldn't use the delegate, use the bad way.
            Connection conn = getConnection();
            Statement statement = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = statement.executeQuery(queryString);
            if (rs.last()) {
                count = rs.getRow();
            } else {
                count = 0;
            }
            rs.close();
            statement.close();
            releaseConnection(conn);
        }
        return count;
    }

    @SuppressWarnings("deprecation")
    private int countByDelegate() throws SQLException {
        int count = -1;
        if (delegate == null) {
            return count;
        }
        /* First try using prepared statement */
        if (delegate instanceof FreeformStatementDelegate) {
            try {
                StatementHelper sh = ((FreeformStatementDelegate) delegate)
                        .getCountStatement();
                Connection c = getConnection();
                PreparedStatement pstmt = c.prepareStatement(sh
                        .getQueryString());
                sh.setParameterValuesToStatement(pstmt);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                count = rs.getInt(1);
                rs.close();
                pstmt.clearParameters();
                pstmt.close();
                releaseConnection(c);
                return count;
            } catch (UnsupportedOperationException e) {
                // Count statement generation not supported
            }
        }
        /* Try using regular statement */
        try {
            String countQuery = delegate.getCountQuery();
            if (countQuery != null) {
                Connection conn = getConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(countQuery);
                rs.next();
                count = rs.getInt(1);
                rs.close();
                statement.close();
                releaseConnection(conn);
                return count;
            }
        } catch (UnsupportedOperationException e) {
            // Count query generation not supported
        }
        return count;
    }

    private Connection getConnection() throws SQLException {
        if (activeConnection != null) {
            return activeConnection;
        }
        return connectionPool.reserveConnection();
    }

    /**
     * Fetches the results for the query. This implementation always fetches the
     * entire record set, ignoring the offset and page length parameters. In
     * order to support lazy loading of records, you must supply a
     * FreeformQueryDelegate that implements the
     * FreeformQueryDelegate.getQueryString(int,int) method.
     * 
     * @throws SQLException
     * 
     * @see FreeformQueryDelegate#getQueryString(int, int)
     */
    @SuppressWarnings("deprecation")
    public ResultSet getResults(int offset, int pagelength) throws SQLException {
        if (activeConnection == null) {
            throw new SQLException("No active transaction!");
        }
        String query = queryString;
        if (delegate != null) {
            /* First try using prepared statement */
            if (delegate instanceof FreeformStatementDelegate) {
                try {
                    StatementHelper sh = ((FreeformStatementDelegate) delegate)
                            .getQueryStatement(offset, pagelength);
                    PreparedStatement pstmt = activeConnection
                            .prepareStatement(sh.getQueryString());
                    sh.setParameterValuesToStatement(pstmt);
                    return pstmt.executeQuery();
                } catch (UnsupportedOperationException e) {
                    // Statement generation not supported, continue...
                }
            }
            try {
                query = delegate.getQueryString(offset, pagelength);
            } catch (UnsupportedOperationException e) {
                // This is fine, we'll just use the default queryString.
            }
        }
        Statement statement = activeConnection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        return rs;
    }

    @SuppressWarnings("deprecation")
    public boolean implementationRespectsPagingLimits() {
        if (delegate == null) {
            return false;
        }
        /* First try using prepared statement */
        if (delegate instanceof FreeformStatementDelegate) {
            try {
                StatementHelper sh = ((FreeformStatementDelegate) delegate)
                        .getCountStatement();
                if (sh != null && sh.getQueryString() != null
                        && sh.getQueryString().length() > 0) {
                    return true;
                }
            } catch (UnsupportedOperationException e) {
                // Statement generation not supported, continue...
            }
        }
        try {
            String queryString = delegate.getQueryString(0, 50);
            return queryString != null && queryString.length() > 0;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.sqlcontainer.query.QueryDelegate#setFilters(java
     * .util.List)
     */
    public void setFilters(List<Filter> filters)
            throws UnsupportedOperationException {
        if (delegate != null) {
            delegate.setFilters(filters);
        } else if (filters != null) {
            throw new UnsupportedOperationException(
                    "FreeFormQueryDelegate not set!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.sqlcontainer.query.QueryDelegate#setOrderBy(java
     * .util.List)
     */
    public void setOrderBy(List<OrderBy> orderBys)
            throws UnsupportedOperationException {
        if (delegate != null) {
            delegate.setOrderBy(orderBys);
        } else if (orderBys != null) {
            throw new UnsupportedOperationException(
                    "FreeFormQueryDelegate not set!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.sqlcontainer.query.QueryDelegate#storeRow(com.vaadin
     * .data.util.sqlcontainer.RowItem)
     */
    public int storeRow(RowItem row) throws SQLException {
        if (activeConnection == null) {
            throw new IllegalStateException("No transaction is active!");
        } else if (primaryKeyColumns.isEmpty()) {
            throw new UnsupportedOperationException(
                    "Cannot store items fetched with a read-only freeform query!");
        }
        if (delegate != null) {
            return delegate.storeRow(activeConnection, row);
        } else {
            throw new UnsupportedOperationException(
                    "FreeFormQueryDelegate not set!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.sqlcontainer.query.QueryDelegate#removeRow(com.vaadin
     * .data.util.sqlcontainer.RowItem)
     */
    public boolean removeRow(RowItem row) throws SQLException {
        if (activeConnection == null) {
            throw new IllegalStateException("No transaction is active!");
        } else if (primaryKeyColumns.isEmpty()) {
            throw new UnsupportedOperationException(
                    "Cannot remove items fetched with a read-only freeform query!");
        }
        if (delegate != null) {
            return delegate.removeRow(activeConnection, row);
        } else {
            throw new UnsupportedOperationException(
                    "FreeFormQueryDelegate not set!");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.sqlcontainer.query.QueryDelegate#beginTransaction()
     */
    public synchronized void beginTransaction()
            throws UnsupportedOperationException, SQLException {
        if (activeConnection != null) {
            throw new IllegalStateException("A transaction is already active!");
        }
        activeConnection = connectionPool.reserveConnection();
        activeConnection.setAutoCommit(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.sqlcontainer.query.QueryDelegate#commit()
     */
    public synchronized void commit() throws UnsupportedOperationException,
            SQLException {
        if (activeConnection == null) {
            throw new SQLException("No active transaction");
        }
        if (!activeConnection.getAutoCommit()) {
            activeConnection.commit();
        }
        connectionPool.releaseConnection(activeConnection);
        activeConnection = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.util.sqlcontainer.query.QueryDelegate#rollback()
     */
    public synchronized void rollback() throws UnsupportedOperationException,
            SQLException {
        if (activeConnection == null) {
            throw new SQLException("No active transaction");
        }
        activeConnection.rollback();
        connectionPool.releaseConnection(activeConnection);
        activeConnection = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.sqlcontainer.query.QueryDelegate#getPrimaryKeyColumns
     * ()
     */
    public List<String> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public String getQueryString() {
        return queryString;
    }

    public FreeformQueryDelegate getDelegate() {
        return delegate;
    }

    public void setDelegate(FreeformQueryDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * This implementation of the containsRowWithKey method rewrites existing
     * WHERE clauses in the query string. The logic is, however, not very
     * complex and some times can do the Wrong Thing<sup>TM</sup>. For the
     * situations where this logic is not enough, you can implement the
     * getContainsRowQueryString method in FreeformQueryDelegate and this will
     * be used instead of the logic.
     * 
     * @see FreeformQueryDelegate#getContainsRowQueryString(Object...)
     * 
     */
    @SuppressWarnings("deprecation")
    public boolean containsRowWithKey(Object... keys) throws SQLException {
        String query = null;
        boolean contains = false;
        if (delegate != null) {
            if (delegate instanceof FreeformStatementDelegate) {
                try {
                    StatementHelper sh = ((FreeformStatementDelegate) delegate)
                            .getContainsRowQueryStatement(keys);
                    Connection c = getConnection();
                    PreparedStatement pstmt = c.prepareStatement(sh
                            .getQueryString());
                    sh.setParameterValuesToStatement(pstmt);
                    ResultSet rs = pstmt.executeQuery();
                    contains = rs.next();
                    rs.close();
                    pstmt.clearParameters();
                    pstmt.close();
                    releaseConnection(c);
                    return contains;
                } catch (UnsupportedOperationException e) {
                    // Statement generation not supported, continue...
                }
            }
            try {
                query = delegate.getContainsRowQueryString(keys);
            } catch (UnsupportedOperationException e) {
                query = modifyWhereClause(keys);
            }
        } else {
            query = modifyWhereClause(keys);
        }
        Connection conn = getConnection();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            contains = rs.next();
            rs.close();
            statement.close();
        } finally {
            releaseConnection(conn);
        }
        return contains;
    }

    /**
     * Releases the connection if it is not part of an active transaction.
     * 
     * @param conn
     *            the connection to release
     */
    private void releaseConnection(Connection conn) {
        if (conn != activeConnection) {
            connectionPool.releaseConnection(conn);
        }
    }

    private String modifyWhereClause(Object... keys) {
        // Build the where rules for the provided keys
        StringBuffer where = new StringBuffer();
        for (int ix = 0; ix < primaryKeyColumns.size(); ix++) {
            where.append(QueryBuilder.quote(primaryKeyColumns.get(ix)));
            if (keys[ix] == null) {
                where.append(" IS NULL");
            } else {
                where.append(" = '").append(keys[ix]).append("'");
            }
            if (ix < primaryKeyColumns.size() - 1) {
                where.append(" AND ");
            }
        }
        // Is there already a WHERE clause in the query string?
        int index = queryString.toLowerCase().indexOf("where ");
        if (index > -1) {
            // Rewrite the where clause
            return queryString.substring(0, index) + "WHERE " + where + " AND "
                    + queryString.substring(index + 6);
        }
        // Append a where clause
        return queryString + " WHERE " + where;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        try {
            rollback();
        } catch (SQLException ignored) {
        }
        out.defaultWriteObject();
    }
}
