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

/**
 * 
 */
package com.vaadin.shared.ui.ui;

import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.ServerRpc;

/**
 * Server RPC methods for the Debug Window.
 * 
 * @since 7.1
 * @author Vaadin Ltd
 */
public interface DebugWindowServerRpc extends ServerRpc {
    /**
     * Sends a request to the server to print details to console that will help
     * the developer to locate the corresponding server-side connector in the
     * source code.
     * 
     * @since 7.1
     * @param connector
     *            the connector to locate
     **/
    public void showServerDebugInfo(Connector connector);

    /**
     * Invokes the layout analyzer on the server
     * 
     * @since 7.1
     */
    public void analyzeLayouts();

}
