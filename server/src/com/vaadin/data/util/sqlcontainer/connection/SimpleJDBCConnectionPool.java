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
    private Thread pingThread = null;

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
        stopPing();
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

    private void stopPing() {
        if (pingThread != null) {
            pingThread.interrupt();
            try {
                pingThread.join(5000);
            } catch (InterruptedException e) {
            }
            pingThread = null;
        }
    }

    /**
     * Performs a trivial query on connections that have not been reserved. If
     * an error occurs on any available connection, all connections will be
     * reset by default. Subclasses can override onPingFailed() to modify this
     * behavior.
     *
     * @throws SQLException
     *             If an error occurs while <strong>resetting</strong> a
     *             connection after a failed ping. This is not thrown for the
     *             failed ping itself.
     */
    synchronized protected void ping() throws SQLException {
        for (Connection cn : availableConnections) {
            Statement st = null;
            try {
                st = cn.createStatement();
                st.execute("select 1");
            } catch (SQLException e) {
                try {
                    onPingFailed(e);
                    break;
                } catch (SQLException e1) {
                    throw e1;
                }

            } finally {
                try {
                    st.close();
                } catch (Exception e) {
                }
                try {
                    if (cn.getAutoCommit() == false) {
                        cn.rollback();
                    }
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * Sends a trivial query to all available connections in the pool. If a
     * query fails, onPingFailed() will be called.
     *
     * @since
     * @param pingIntervalInSeconds
     *            The frequency (in seconds) to send the ping query. Set to '0'
     *            to disable the pink.
     */
    public void setPingInterval(final int pingIntervalInSeconds) {
        stopPing();
        if (pingIntervalInSeconds > 0) {
            pingThread = new Thread(new Runnable() {
                public void run() {

                    while (true) {
                        try {
                            Thread.sleep(pingIntervalInSeconds * 1000);
                        } catch (InterruptedException e) {
                            return;
                        }
                        try {
                            ping();
                        } catch (SQLException e) {
                            // e.printStackTrace();
                        }
                    }
                }
            });
            pingThread.start();
        }
    }

    /**
     * The default behavior is to recreate all connections in the available
     * queue.
     *
     * @throws SQLException
     */
    synchronized void onPingFailed(SQLException exception) throws SQLException {
        System.err.println(
                "SQL Database ping failed. Recreating available connections");

        for (Connection c : availableConnections) {
            try {
                c.close();
            } catch (SQLException e) {
                // No need to do anything
            }
        }
        int size = availableConnections.size();
        availableConnections.clear();
        for (int i = 0; i < size; i++) {
            availableConnections.add(createConnection());
        }
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        initialized = false;
        out.defaultWriteObject();
    }

}
