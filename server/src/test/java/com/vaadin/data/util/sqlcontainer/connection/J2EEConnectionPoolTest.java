package com.vaadin.data.util.sqlcontainer.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

public class J2EEConnectionPoolTest {

    @Test
    public void reserveConnection_dataSourceSpecified_shouldReturnValidConnection()
            throws SQLException {
        Connection connection = EasyMock.createMock(Connection.class);
        connection.setAutoCommit(false);
        EasyMock.expectLastCall();
        DataSource ds = EasyMock.createMock(DataSource.class);
        ds.getConnection();
        EasyMock.expectLastCall().andReturn(connection);
        EasyMock.replay(connection, ds);

        J2EEConnectionPool pool = new J2EEConnectionPool(ds);
        Connection c = pool.reserveConnection();
        Assert.assertEquals(connection, c);
        EasyMock.verify(connection, ds);
    }

    @Test
    public void releaseConnection_shouldCloseConnection() throws SQLException {
        Connection connection = EasyMock.createMock(Connection.class);
        connection.setAutoCommit(false);
        EasyMock.expectLastCall();
        connection.close();
        EasyMock.expectLastCall();
        DataSource ds = EasyMock.createMock(DataSource.class);
        ds.getConnection();
        EasyMock.expectLastCall().andReturn(connection);
        EasyMock.replay(connection, ds);

        J2EEConnectionPool pool = new J2EEConnectionPool(ds);
        Connection c = pool.reserveConnection();
        Assert.assertEquals(connection, c);
        pool.releaseConnection(c);
        EasyMock.verify(connection, ds);
    }

    @Test
    public void reserveConnection_dataSourceLookedUp_shouldReturnValidConnection()
            throws SQLException, NamingException {
        Connection connection = EasyMock.createMock(Connection.class);
        connection.setAutoCommit(false);
        EasyMock.expectLastCall();
        connection.close();
        EasyMock.expectLastCall();

        DataSource ds = EasyMock.createMock(DataSource.class);
        ds.getConnection();
        EasyMock.expectLastCall().andReturn(connection);

        System.setProperty("java.naming.factory.initial",
                "com.vaadin.data.util.sqlcontainer.connection.MockInitialContextFactory");
        Context context = EasyMock.createMock(Context.class);
        context.lookup("testDataSource");
        EasyMock.expectLastCall().andReturn(ds);
        MockInitialContextFactory.setMockContext(context);

        EasyMock.replay(context, connection, ds);

        J2EEConnectionPool pool = new J2EEConnectionPool("testDataSource");
        Connection c = pool.reserveConnection();
        Assert.assertEquals(connection, c);
        pool.releaseConnection(c);
        EasyMock.verify(context, connection, ds);
    }

    @Test(expected = SQLException.class)
    public void reserveConnection_nonExistantDataSourceLookedUp_shouldFail()
            throws SQLException, NamingException {
        System.setProperty("java.naming.factory.initial",
                "com.vaadin.addon.sqlcontainer.connection.MockInitialContextFactory");
        Context context = EasyMock.createMock(Context.class);
        context.lookup("foo");
        EasyMock.expectLastCall().andThrow(new NamingException("fail"));
        MockInitialContextFactory.setMockContext(context);

        EasyMock.replay(context);

        J2EEConnectionPool pool = new J2EEConnectionPool("foo");
        pool.reserveConnection();
        EasyMock.verify(context);
    }

    @Test
    public void releaseConnection_null_shouldSucceed() throws SQLException {
        DataSource ds = EasyMock.createMock(DataSource.class);
        EasyMock.replay(ds);

        J2EEConnectionPool pool = new J2EEConnectionPool(ds);
        pool.releaseConnection(null);
        EasyMock.verify(ds);
    }
}
