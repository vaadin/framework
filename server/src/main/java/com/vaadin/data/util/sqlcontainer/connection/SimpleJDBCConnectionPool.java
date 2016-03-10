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
package com.vaadin.data.util.sqlcontainer.connection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple implementation of the JDBCConnectionPool interface. Handles loading
 * the JDBC driver, setting up the connections and ensuring they are still
 * usable upon release.
 */
@SuppressWarnings("serial")
public class SimpleJDBCConnectionPool implements JDBCConnectionPool {

    private int initialConnections = 5;
    private int maxConnections = 20;

    private String driverName;
    private String connectionUri;
    private String userName;
    private String password;

    private transient Set<Connection> availableConnections;
    private transient Set<Connection> reservedConnections;

    private boolean initialized;

    public SimpleJDBCConnectionPool(String driverName, String connectionUri,
            String userName, String password) throws SQLException {
        if (driverName == null) {
            throw new IllegalArgumentException(
                    "JDBC driver class name must be given.");
        }
        if (connectionUri == null) {
            throw new IllegalArgumentException(
                    "Database connection URI must be given.");
        }
        if (userName == null) {
            throw new IllegalArgumentException(
                    "Database username must be given.");
        }
        if (password == null) {
            throw new IllegalArgumentException(
                    "Database password must be given.");
        }
        this.driverName = driverName;
        this.connectionUri = connectionUri;
        this.userName = userName;
        this.password = password;

        /* Initialize JDBC driver */
        try {
            Class.forName(driverName).newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Specified JDBC Driver: " + driverName
                    + " - initialization failed.", ex);
        }
    }

    public SimpleJDBCConnectionPool(String driverName, String connectionUri,
            String userName, String password, int initialConnections,
            int maxConnections) throws SQLException {
        this(driverName, connectionUri, userName, password);
        this.initialConnections = initialConnections;
        this.maxConnections = maxConnections;
    }

    private void initializeConnections() throws SQLException {
        availableConnections = new HashSet<Connection>(initialConnections);
        reservedConnections = new HashSet<Connection>(initialConnections);
        for (int i = 0; i < initialConnections; i++) {
            availableConnections.add(createConnection());
        }
        initialized = true;
    }

    @Override
    public synchronized Connection reserveConnection() throws SQLException {
        if (!initialized) {
            initializeConnections();
        }
        if (availableConnections.isEmpty()) {
            if (reservedConnections.size() < maxConnections) {
                availableConnections.add(createConnection());
            } else {
                throw new SQLException("Connection limit has been reached.");
            }
        }

        Connection c = availableConnections.iterator().next();
        availableConnections.remove(c);
        reservedConnections.add(c);

        return c;
    }

    @Override
    public synchronized void releaseConnection(Connection conn) {
        if (conn == null || !initialized) {
            return;
        }
        /* Try to roll back if necessary */
        try {
            if (!conn.getAutoCommit()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            /* Roll back failed, close and discard connection */
            try {
                conn.close();
            } catch (SQLException e1) {
                /* Nothing needs to be done */
            }
            reservedConnections.remove(conn);
            return;
        }
        reservedConnections.remove(conn);
        availableConnections.add(conn);
    }

    private Connection createConnection() throws SQLException {
        Connection c = DriverManager.getConnection(connectionUri, userName,
                password);
        c.setAutoCommit(false);
        if (driverName.toLowerCase().contains("mysql")) {
            try {
                Statement s = c.createStatement();
                s.execute("SET SESSION sql_mode = 'ANSI'");
                s.close();
            } catch (Exception e) {
                // Failed to set ansi mode; continue
            }
        }
        return c;
    }

    @Override
    public void destroy() {
        for (Connection c : availableConnections) {
            try {
                c.close();
            } catch (SQLException e) {
                // No need to do anything
            }
        }
        for (Connection c : reservedConnections) {
            try {
                c.close();
            } catch (SQLException e) {
                // No need to do anything
            }
        }

    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        initialized = false;
        out.defaultWriteObject();
    }

}
