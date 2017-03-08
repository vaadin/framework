/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.v7.data.util.sqlcontainer.connection;

import java.sql.Connection;
import java.sql.SQLException;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.util.sqlcontainer.SQLTestsConstants;
import com.vaadin.v7.data.util.sqlcontainer.query.ValidatingSimpleJDBCConnectionPool;

public class SimpleJDBCConnectionPoolTest {
    private JDBCConnectionPool connectionPool;

    @Before
    public void setUp() throws SQLException {
        connectionPool = new ValidatingSimpleJDBCConnectionPool(
                SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL,
                SQLTestsConstants.dbUser, SQLTestsConstants.dbPwd, 2, 2);
    }

    @Test
    public void reserveConnection_reserveNewConnection_returnsConnection()
            throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        Assert.assertNotNull(conn);
    }

    @Test
    public void releaseConnection_releaseUnused_shouldNotThrowException()
            throws SQLException {
        Connection conn = connectionPool.reserveConnection();
        connectionPool.releaseConnection(conn);
        Assert.assertFalse(conn.isClosed());
    }

    @Test(expected = SQLException.class)
    public void reserveConnection_noConnectionsLeft_shouldFail()
            throws SQLException {
        try {
            connectionPool.reserveConnection();
            connectionPool.reserveConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail(
                    "Exception before all connections used! " + e.getMessage());
        }

        connectionPool.reserveConnection();
        Assert.fail(
                "Reserving connection didn't fail even though no connections are available!");
    }

    @Test
    public void reserveConnection_oneConnectionLeft_returnsConnection()
            throws SQLException {
        try {
            connectionPool.reserveConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail(
                    "Exception before all connections used! " + e.getMessage());
        }

        Connection conn = connectionPool.reserveConnection();
        Assert.assertNotNull(conn);
    }

    @Test
    public void reserveConnection_oneConnectionJustReleased_returnsConnection()
            throws SQLException {
        Connection conn2 = null;
        try {
            connectionPool.reserveConnection();
            conn2 = connectionPool.reserveConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail(
                    "Exception before all connections used! " + e.getMessage());
        }

        connectionPool.releaseConnection(conn2);

        connectionPool.reserveConnection();
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_allParametersNull_shouldFail() throws SQLException {
        SimpleJDBCConnectionPool cp = new SimpleJDBCConnectionPool(null, null,
                null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_onlyDriverNameGiven_shouldFail() throws SQLException {
        SimpleJDBCConnectionPool cp = new SimpleJDBCConnectionPool(
                SQLTestsConstants.dbDriver, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_onlyDriverNameAndUrlGiven_shouldFail()
            throws SQLException {
        SimpleJDBCConnectionPool cp = new SimpleJDBCConnectionPool(
                SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL, null,
                null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_onlyDriverNameAndUrlAndUserGiven_shouldFail()
            throws SQLException {
        SimpleJDBCConnectionPool cp = new SimpleJDBCConnectionPool(
                SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL,
                SQLTestsConstants.dbUser, null);
    }

    @Test(expected = RuntimeException.class)
    public void construct_nonExistingDriver_shouldFail() throws SQLException {
        SimpleJDBCConnectionPool cp = new SimpleJDBCConnectionPool("foo",
                SQLTestsConstants.dbURL, SQLTestsConstants.dbUser,
                SQLTestsConstants.dbPwd);
    }

    @Test
    public void reserveConnection_newConnectionOpened_shouldSucceed()
            throws SQLException {
        connectionPool = new SimpleJDBCConnectionPool(
                SQLTestsConstants.dbDriver, SQLTestsConstants.dbURL,
                SQLTestsConstants.dbUser, SQLTestsConstants.dbPwd, 0, 2);
        Connection c = connectionPool.reserveConnection();
        Assert.assertNotNull(c);
    }

    @Test
    public void releaseConnection_nullConnection_shouldDoNothing() {
        connectionPool.releaseConnection(null);
    }

    @Test
    public void releaseConnection_failingRollback_shouldCallClose()
            throws SQLException {
        Connection c = EasyMock.createMock(Connection.class);
        c.getAutoCommit();
        EasyMock.expectLastCall().andReturn(false);
        c.rollback();
        EasyMock.expectLastCall().andThrow(new SQLException("Rollback failed"));
        c.close();
        EasyMock.expectLastCall().atLeastOnce();
        EasyMock.replay(c);
        // make sure the connection pool is initialized
        // Bypass validation
        JDBCConnectionPool realPool = ((ValidatingSimpleJDBCConnectionPool) connectionPool)
                .getRealPool();
        realPool.reserveConnection();
        realPool.releaseConnection(c);
        EasyMock.verify(c);
    }

    @Test
    public void destroy_shouldCloseAllConnections() throws SQLException {
        Connection c1 = connectionPool.reserveConnection();
        Connection c2 = connectionPool.reserveConnection();
        try {
            connectionPool.destroy();
        } catch (RuntimeException e) {
            // The test connection pool throws an exception when the pool was
            // not empty but only after cleanup of the real pool has been done
        }

        Assert.assertTrue(c1.isClosed());
        Assert.assertTrue(c2.isClosed());
    }

    @Test
    public void destroy_shouldCloseAllConnections2() throws SQLException {
        Connection c1 = connectionPool.reserveConnection();
        Connection c2 = connectionPool.reserveConnection();
        connectionPool.releaseConnection(c1);
        connectionPool.releaseConnection(c2);
        connectionPool.destroy();
        Assert.assertTrue(c1.isClosed());
        Assert.assertTrue(c2.isClosed());
    }

}
