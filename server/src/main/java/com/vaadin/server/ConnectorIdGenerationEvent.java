/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.server;

import java.util.EventObject;

/**
 * Event object containing information related to connector id generation.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class ConnectorIdGenerationEvent extends EventObject {

    private final VaadinSession session;
    private final ClientConnector connector;

    /**
     * Creates a new event for the given session and connector.
     *
     * @param session
     *            the session for which a connector id is needed, not
     *            <code>null</code>
     * @param connector
     *            the connector that should get an id, not <code>null</code>
     */
    public ConnectorIdGenerationEvent(VaadinSession session,
            ClientConnector connector) {
        super(session.getService());

        assert session != null;
        assert connector != null;

        this.session = session;
        this.connector = connector;
    }

    /**
     * Gets the session for which connector id is needed.
     *
     * @return the session, not <code>null</code>
     */
    public VaadinSession getSession() {
        return session;
    }

    /**
     * Gets the connector that should get an id.
     *
     * @return the connector, not <code>null</code>
     */
    public ClientConnector getConnector() {
        return connector;
    }

    @Override
    public VaadinService getSource() {
        return (VaadinService) super.getSource();
    }
}
