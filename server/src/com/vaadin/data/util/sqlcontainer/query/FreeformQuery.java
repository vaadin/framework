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
public class FreeformQuery extends AbstractTransactionalQuery implements
        QueryDelegate {

    FreeformQueryDelegate delegate = null;
    private String queryString;
    private List<String> primaryKeyColumns;

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
     * @deprecated As of 6.7, @see
     *             {@link FreeformQuery#FreeformQuery(String, JDBCConnectionPool, String...)}
     */
    @Deprecated
    public FreeformQuery(String queryString, List<String> primaryKeyColumns,
            JDBCConnectionPool connectionPool) {
        super(connectionPool);
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
    @Override
    public int getCount() throws SQLException {
        // First try the delegate
        int count = countByDelegate();
        if (count < 0) {
            // Couldn't use the delegate, use the bad way.
            Statement statement = null;
            ResultSet rs = null;
            Connection conn = getConnection();
            try {
                statement = conn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                rs = statement.executeQuery(queryString);
                if (rs.last()) {
                    count = rs.getRow();
                } else {
                    count = 0;
                }
            } finally {
                releaseConnection(conn, statement, rs);
            }
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
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                Connection c = getConnection();
                try {
                    pstmt = c.prepareStatement(sh.getQueryString());
                    sh.setParameterValuesToStatement(pstmt);
                    rs = pstmt.executeQuery();
                    rs.next();
                    count = rs.getInt(1);
                } finally {
                    releaseConnection(c, pstmt, rs);
                }
                return count;
            } catch (UnsupportedOperationException e) {
                // Count statement generation not supported
            }
        }
        /* Try using regular statement */
        try {
            String countQuery = delegate.getCountQuery();
            if (countQuery != null) {
                Statement statement = null;
                ResultSet rs = null;
                Connection conn = getConnection();
                try {
                    statement = conn.createStatement();
                    rs = statement.executeQuery(countQuery);
                    rs.next();
                    count = rs.getInt(1);
                    return count;
                } finally {
                    releaseConnection(conn, statement, rs);
                }
            }
        } catch (UnsupportedOperationException e) {
            // Count query generation not supported
        }
        return count;
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
    @Override
    @SuppressWarnings({ "deprecation", "finally" })
    public ResultSet getResults(int offset, int pagelength) throws SQLException {
        ensureTransaction();
        String query = queryString;
        if (delegate != null) {
            /* First try using prepared statement */
            if (delegate instanceof FreeformStatementDelegate) {
                try {
                    StatementHelper sh = ((FreeformStatementDelegate) delegate)
                            .getQueryStatement(offset, pagelength);
                    PreparedStatement pstmt = getConnection().prepareStatement(
                            sh.getQueryString());
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
        Statement statement = getConnection().createStatement();
        ResultSet rs;
        try {
            rs = statement.executeQuery(query);
        } catch (SQLException e) {
            try {
                statement.close();
            } finally {
                // throw the original exception even if closing the statement
                // fails
                throw e;
            }
        }
        return rs;
    }

    @Override
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
    @Override
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
    @Override
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
    @Override
    public int storeRow(RowItem row) throws SQLException {
        if (!isInTransaction()) {
            throw new IllegalStateException("No transaction is active!");
        } else if (primaryKeyColumns.isEmpty()) {
            throw new UnsupportedOperationException(
                    "Cannot store items fetched with a read-only freeform query!");
        }
        if (delegate != null) {
            return delegate.storeRow(getConnection(), row);
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
    @Override
    public boolean removeRow(RowItem row) throws SQLException {
        if (!isInTransaction()) {
            throw new IllegalStateException("No transaction is active!");
        } else if (primaryKeyColumns.isEmpty()) {
            throw new UnsupportedOperationException(
                    "Cannot remove items fetched with a read-only freeform query!");
        }
        if (delegate != null) {
            return delegate.removeRow(getConnection(), row);
        } else {
            throw new UnsupportedOperationException(
                    "FreeFormQueryDelegate not set!");
        }
    }

    @Override
    public synchronized void beginTransaction()
            throws UnsupportedOperationException, SQLException {
        super.beginTransaction();
    }

    @Override
    public synchronized void commit() throws UnsupportedOperationException,
            SQLException {
        super.commit();
    }

    @Override
    public synchronized void rollback() throws UnsupportedOperationException,
            SQLException {
        super.rollback();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.util.sqlcontainer.query.QueryDelegate#getPrimaryKeyColumns
     * ()
     */
    @Override
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
    @Override
    @SuppressWarnings("deprecation")
    public boolean containsRowWithKey(Object... keys) throws SQLException {
        String query = null;
        boolean contains = false;
        if (delegate != null) {
            if (delegate instanceof FreeformStatementDelegate) {
                try {
                    StatementHelper sh = ((FreeformStatementDelegate) delegate)
                            .getContainsRowQueryStatement(keys);

                    PreparedStatement pstmt = null;
                    ResultSet rs = null;
                    Connection c = getConnection();
                    try {
                        pstmt = c.prepareStatement(sh.getQueryString());
                        sh.setParameterValuesToStatement(pstmt);
                        rs = pstmt.executeQuery();
                        contains = rs.next();
                        return contains;
                    } finally {
                        releaseConnection(c, pstmt, rs);
                    }
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
        Statement statement = null;
        ResultSet rs = null;
        Connection conn = getConnection();
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery(query);
            contains = rs.next();
        } finally {
            releaseConnection(conn, statement, rs);
        }
        return contains;
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
