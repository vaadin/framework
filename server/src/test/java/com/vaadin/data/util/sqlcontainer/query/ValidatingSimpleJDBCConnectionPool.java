package com.vaadin.data.util.sqlcontainer.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;

/**
 * Connection pool for testing SQLContainer. Ensures that only reserved
 * connections are released and that all connections are released before the
 * pool is destroyed.
 *
 * @author Vaadin Ltd
 */
public class ValidatingSimpleJDBCConnectionPool implements JDBCConnectionPool {

    private JDBCConnectionPool realPool;
    private Set<Connection> reserved = new HashSet<Connection>();
    private Set<Connection> alreadyReleased = new HashSet<Connection>();

    public ValidatingSimpleJDBCConnectionPool(String driverName,
            String connectionUri, String userName, String password,
            int initialConnections, int maxConnections) throws SQLException {
        realPool = new SimpleJDBCConnectionPool(driverName, connectionUri,
                userName, password, initialConnections, maxConnections);
    }

    @Deprecated
    public JDBCConnectionPool getRealPool() {
        return realPool;
    }

    @Override
    public Connection reserveConnection() throws SQLException {
        Connection c = realPool.reserveConnection();
        reserved.add(c);
        return c;
    }

    @Override
    public void releaseConnection(Connection conn) {
        if (conn != null && !reserved.remove(conn)) {
            if (alreadyReleased.contains(conn)) {
                getLogger().severe("Tried to release connection (" + conn
                        + ") which has already been released");
            } else {
                throw new RuntimeException("Tried to release connection ("
                        + conn + ") not reserved using reserveConnection");
            }
        }
        realPool.releaseConnection(conn);
        alreadyReleased.add(conn);

    }

    @Override
    public void destroy() {
        realPool.destroy();
        if (!reserved.isEmpty()) {
            throw new RuntimeException(
                    reserved.size() + " connections never released");
        }
    }

    private static Logger getLogger() {
        return Logger
                .getLogger(ValidatingSimpleJDBCConnectionPool.class.getName());
    }
}