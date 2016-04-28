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
package com.vaadin.shared;

import java.io.Serializable;

/**
 * Interface implemented by all classes that are capable of communicating with
 * the server or the client side.
 * <p>
 * A connector consists of a shared state (server sets the state and
 * automatically communicates changes to the client) and the possibility to do
 * RPC calls either from the server to the client or from the client to the
 * server.
 * </p>
 * <p>
 * No classes should implement this interface directly, client side classes
 * wanting to communicate with server side should implement
 * {@link com.vaadin.client.ServerConnector} and server side classes should
 * implement {@link com.vaadin.server.ClientConnector}.
 * </p>
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public interface Connector extends Serializable {
    /**
     * Returns the id for this connector. This is set by the framework and does
     * not change during the lifetime of a connector.
     * 
     * @return The id for the connector.
     */
    public String getConnectorId();

    /**
     * Gets the parent connector of this connector, or <code>null</code> if the
     * connector is not attached to any parent.
     * 
     * @return the parent connector, or <code>null</code> if there is no parent.
     */
    public Connector getParent();

}
