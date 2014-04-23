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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;

/**
 * Common base class for database query classes that handle connections and
 * transactions.
 * 
 * @author Vaadin Ltd
 * @since 6.8.9
 */
public abstract class AbstractTransactionalQuery implements Serializable {

    private JDBCConnectionPool connectionPool;
    private transient Connection activeConnection;

    AbstractTransactionalQuery() {
    }

    AbstractTransactionalQuery(JDBCConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    /**
     * Reserves a connection with auto-commit off if no transaction is in
     * progress.
     * 
     * @throws IllegalStateException
     *             if a transaction is already open
     * @throws SQLException
     *             if a connection could not be obtained or configured
     */
    public void beginTransaction() throws UnsupportedOperationException,
            SQLException {
        if (isInTransaction()) {
            throw new IllegalStateException("A transaction is already active!");
        }
        activeConnection = connectionPool.reserveConnection();
        activeConnection.setAutoCommit(false);
    }

    /**
     * Commits (if not in auto-commit mode) and releases the active connection.
     * 
     * @throws SQLException
     *             if not in a transaction managed by this query
     */
    public void commit() throws UnsupportedOperationException, SQLException {
        if (!isInTransaction()) {
            throw new SQLException("No active transaction");
        }
        if (!activeConnection.getAutoCommit()) {
            activeConnection.commit();
        }
        connectionPool.releaseConnection(activeConnection);
        activeConnection = null;
    }

    /**
     * Rolls back and releases the active connection.
     * 
     * @throws SQLException
     *             if not in a transaction managed by this query
     */
    public void rollback() throws UnsupportedOperationException, SQLException {
        if (!isInTransaction()) {
            throw new SQLException("No active transaction");
        }
        activeConnection.rollback();
        connectionPool.releaseConnection(activeConnection);
        activeConnection = null;
    }

    /**
     * Check that a transaction is active.
     * 
     * @throws SQLException
     *             if no active transaction
     */
    protected void ensureTransaction() throws SQLException {
        if (!isInTransaction()) {
            throw new SQLException("No active transaction!");
        }
    }

    /**
     * Closes a statement and a resultset, then releases the connection if it is
     * not part of an active transaction. A failure in closing one of the
     * parameters does not prevent closing the rest.
     * 
     * If the statement is a {@link PreparedStatement}, its parameters are
     * cleared prior to closing the statement.
     * 
     * Although JDBC specification does state that closing a statement closes
     * its result set and closing a connection closes statements and result
     * sets, this method does try to close the result set and statement
     * explicitly whenever not null. This can guard against bugs in certain JDBC
     * drivers and reduce leaks in case e.g. closing the result set succeeds but
     * closing the statement or connection fails.
     * 
     * @param conn
     *            the connection to release
     * @param statement
     *            the statement to close, may be null to skip closing
     * @param rs
     *            the result set to close, may be null to skip closing
     * @throws SQLException
     *             if closing the result set or the statement fails
     */
    protected void releaseConnection(Connection conn, Statement statement,
            ResultSet rs) throws SQLException {
        try {
            try {
                if (null != rs) {
                    rs.close();
                }
            } finally {
                if (null != statement) {
                    if (statement instanceof PreparedStatement) {
                        try {
                            ((PreparedStatement) statement).clearParameters();
                        } catch (Exception e) {
                            // will be closed below anyway
                        }
                    }
                    statement.close();
                }
            }
        } finally {
            releaseConnection(conn);
        }
    }

    /**
     * Returns the currently active connection, reserves and returns a new
     * connection if no active connection.
     * 
     * @return previously active or newly reserved connection
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        if (activeConnection != null) {
            return activeConnection;
        }
        return connectionPool.reserveConnection();
    }

    protected boolean isInTransaction() {
        return activeConnection != null;
    }

    /**
     * Releases the connection if it is not part of an active transaction.
     * 
     * @param conn
     *            the connection to release
     */
    private void releaseConnection(Connection conn) {
        if (conn != activeConnection && conn != null) {
            connectionPool.releaseConnection(conn);
        }
    }
}
