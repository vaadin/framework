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
package com.vaadin.terminal.gwt.client.communication;

import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * Initialization support for client to server RPC interfaces.
 * 
 * This is in a separate interface used by the GWT generator class. The init
 * method is not in {@link ServerRpc} because then also server side proxies
 * would have to implement the initialization method.
 * 
 * @since 7.0
 */
public interface InitializableServerRpc extends ServerRpc {
    /**
     * Associates the RPC proxy with a connector. Called by generated code.
     * Should never be called manually.
     * 
     * @param connector
     *            The connector the ServerRPC instance is assigned to.
     */
    public void initRpc(ServerConnector connector);
}