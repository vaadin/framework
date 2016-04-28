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
package com.vaadin.event;

import java.util.logging.Logger;

import com.vaadin.event.Action.Container;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.VariableOwner;
import com.vaadin.server.communication.ServerRpcHandler;
import com.vaadin.ui.Component;

/**
 * An ActionManager connected to a connector. Takes care of verifying that the
 * connector can receive events before triggering an action.
 * <p>
 * This is mostly a workaround until shortcut actions are re-implemented in a
 * more sensible way.
 * 
 * @since 7.1.8
 * @author Vaadin Ltd
 */
public class ConnectorActionManager extends ActionManager {

    private ClientConnector connector;

    /**
     * Initialize an action manager for the given connector.
     * 
     * @param connector
     *            the owner of this action manager
     */
    public ConnectorActionManager(ClientConnector connector) {
        super();
        this.connector = connector;
    }

    /**
     * Initialize an action manager for the given connector using the given
     * viewer.
     * 
     * @param connector
     *            the owner of this action manager
     * @param viewer
     *            the viewer connected
     */
    public <T extends Component & Container & VariableOwner> ConnectorActionManager(
            ClientConnector connector, T viewer) {
        super(viewer);
        this.connector = connector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.event.ActionManager#handleAction(com.vaadin.event.Action,
     * java.lang.Object, java.lang.Object)
     */
    @Override
    public void handleAction(Action action, Object sender, Object target) {
        if (!connector.isConnectorEnabled()) {
            getLogger().warning(
                    ServerRpcHandler.getIgnoredDisabledError("action",
                            connector));
            return;
        }

        super.handleAction(action, sender, target);
    }

    private static final Logger getLogger() {
        return Logger.getLogger(ConnectorActionManager.class.getName());
    }

}
