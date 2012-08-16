/*
 * Copyright 2011 Vaadin Ltd.
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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for implementing connection pools to be used with SQLContainer.
 */
public interface JDBCConnectionPool extends Serializable {
    /**
     * Retrieves a connection.
     * 
     * @return a usable connection to the database
     * @throws SQLException
     */
    public Connection reserveConnection() throws SQLException;

    /**
     * Releases a connection that was retrieved earlier.
     * 
     * Note that depending on implementation, the transaction possibly open in
     * the connection may or may not be rolled back.
     * 
     * @param conn
     *            Connection to be released
     */
    public void releaseConnection(Connection conn);

    /**
     * Destroys the connection pool: close() is called an all the connections in
     * the pool, whether available or reserved.
     * 
     * This method was added to fix PostgreSQL -related issues with connections
     * that were left hanging 'idle'.
     */
    public void destroy();
}
