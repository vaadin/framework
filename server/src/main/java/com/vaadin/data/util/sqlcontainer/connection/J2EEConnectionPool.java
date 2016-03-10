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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class J2EEConnectionPool implements JDBCConnectionPool {

    private String dataSourceJndiName;

    private DataSource dataSource = null;

    public J2EEConnectionPool(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public J2EEConnectionPool(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
    }

    @Override
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

    @Override
    public void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                Logger.getLogger(J2EEConnectionPool.class.getName()).log(
                        Level.FINE, "Could not release SQL connection", e);
            }
        }
    }

    @Override
    public void destroy() {
        dataSource = null;
    }

}
