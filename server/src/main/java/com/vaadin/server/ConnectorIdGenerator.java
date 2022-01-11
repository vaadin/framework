/*
 * Copyright 2000-2022 Vaadin Ltd.
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

import java.io.Serializable;

/**
 * Callback for generating the id for new connectors. A generator can be
 * registered to be used with an application by overriding
 * {@link VaadinService#initConnectorIdGenerator(java.util.List)} or by calling
 * {@link ServiceInitEvent#addConnectorIdGenerator(ConnectorIdGenerator)} from a
 * {@link VaadinServiceInitListener}.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@FunctionalInterface
public interface ConnectorIdGenerator extends Serializable {
    /**
     * Generates a connector id for a connector.
     *
     * @param event
     *            the event object that has a reference to the connector and the
     *            session, not <code>null</code>
     * @return the connector id to use for the connector, not <code>null</code>
     */
    public String generateConnectorId(ConnectorIdGenerationEvent event);

    /**
     * Generates a connector id using the default logic by using
     * {@link VaadinSession#getNextConnectorId()}.
     *
     * @param event
     *            the event object that has a reference to the connector and the
     *            session, not <code>null</code>
     * @return the connector id to use for the connector, not <code>null</code>
     */
    public static String generateDefaultConnectorId(
            ConnectorIdGenerationEvent event) {
        assert event != null;
        return event.getSession().getNextConnectorId();
    }
}
