/*
 * Copyright 2000-2018 Vaadin Ltd.
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

import java.util.EventObject;

import com.vaadin.server.ClientConnector;

/**
 * A base class for user interface events fired by connectors.
 *
 * @author Vaadin Ltd.
 * @since 7.0
 */
public abstract class ConnectorEvent extends EventObject {

    /**
     * Creates a new event fired by the given source.
     *
     * @param source
     *            the source connector
     */
    public ConnectorEvent(ClientConnector source) {
        super(source);
    }

    /**
     * Returns the connector that fired the event.
     *
     * @return the source connector
     */
    public ClientConnector getConnector() {
        return (ClientConnector) getSource();
    }
}
