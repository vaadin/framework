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
package com.vaadin.client.communication;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.client.ServerConnector;

public abstract class AbstractServerConnectorEvent<H extends EventHandler>
        extends GwtEvent<H> {
    private ServerConnector connector;

    protected AbstractServerConnectorEvent() {
    }

    public ServerConnector getConnector() {
        return connector;
    }

    public void setConnector(ServerConnector connector) {
        this.connector = connector;
    }

    /**
     * Sends this event to the given handler.
     * 
     * @param handler
     *            The handler to dispatch.
     */
    @Override
    public abstract void dispatch(H handler);
}
