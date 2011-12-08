/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class J2EEConnectionPool implements JDBCConnectionPool {
    private static final Logger logger = Logger
            .getLogger(J2EEConnectionPool.class.getName());

    private String dataSourceJndiName;

    private DataSource dataSource = null;

    public J2EEConnectionPool(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public J2EEConnectionPool(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
    }

    public Connection reserveConnection() throws SQLException {
        Connection conn = getDataSource().getConnection();
        conn.setAutoCommit(false);

        return conn;
    }

    private DataSource getDataSource() throws SQLException {
        if (dataSource == null) {
            dataSource = lookupDataSource();
        }
        return dataSource;
    }

    private DataSource lookupDataSource() throws SQLException {
        try {
            InitialContext ic = new InitialContext();
            return (DataSource) ic.lookup(dataSourceJndiName);
        } catch (NamingException e) {
            throw new SQLException(
                    "NamingException - Cannot connect to the database. Cause: "
                            + e.getMessage());
        }
    }

    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.log(Level.FINE, "Could not release SQL connection", e);
            }
        }
    }

    public void destroy() {
        dataSource = null;
    }

}