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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;
import java.util.Set;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.PaintException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonException;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

/**
 * Serializes a connector hierarchy to JSON.
 *
 * @author Vaadin Ltd
 * @since 7.1
 */
public class ConnectorHierarchyWriter implements Serializable {

    /**
     * Writes a JSON object containing the connector hierarchy (parent-child
     * mappings) of the dirty connectors in the given UI.
     *
     * @param ui
     *            The {@link UI} whose hierarchy to write.
     * @param writer
     *            The {@link Writer} used to write the JSON.
     * @param stateUpdateConnectors
     *            connector ids with state changes
     * @throws IOException
     *             If the serialization fails.
     */
    public void write(UI ui, Writer writer, Set<String> stateUpdateConnectors)
            throws IOException {

        Collection<ClientConnector> dirtyVisibleConnectors = ui
                .getConnectorTracker().getDirtyVisibleConnectors();

        JsonObject hierarchyInfo = Json.createObject();
        for (ClientConnector connector : dirtyVisibleConnectors) {
            String connectorId = connector.getConnectorId();
            JsonArray children = Json.createArray();

            for (ClientConnector child : AbstractClientConnector
                    .getAllChildrenIterable(connector)) {
                if (LegacyCommunicationManager
                        .isConnectorVisibleToClient(child)) {
                    children.set(children.length(), child.getConnectorId());
                }
            }

            // Omit for leaf nodes with state changes
            if (children.length() > 0
                    || !stateUpdateConnectors.contains(connectorId)) {
                try {
                    hierarchyInfo.put(connectorId, children);
                } catch (JsonException e) {
                    throw new PaintException(
                            "Failed to send hierarchy information about "
                                    + connectorId + " to the client: "
                                    + e.getMessage(),
                            e);
                }
            }
        }
        // Dummy assert just for conditionally storing away data that will be
        // used by the real assert later on
        assert storeSentHierarchy(hierarchyInfo, stateUpdateConnectors);

        writer.write(JsonUtil.stringify(hierarchyInfo));
    }

    private boolean storeSentHierarchy(JsonObject hierarchyInfo,
            Set<String> stateUpdateConnectors) {
        VaadinRequest request = VaadinService.getCurrentRequest();
        if (request != null) {
            request.setAttribute(
                    ConnectorHierarchyWriter.class.getName() + ".hierarchyInfo",
                    hierarchyInfo);
            request.setAttribute(ConnectorHierarchyWriter.class.getName()
                    + ".stateUpdateConnectors", stateUpdateConnectors);
        }

        // Always true, we're just setting up for another assert
        return true;
    }

}
