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

package com.vaadin.server.communication;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.PaintException;
import com.vaadin.ui.UI;

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
     * @throws IOException
     *             If the serialization fails.
     */
    public void write(UI ui, Writer writer) throws IOException {

        Collection<ClientConnector> dirtyVisibleConnectors = ui
                .getConnectorTracker().getDirtyVisibleConnectors();

        JSONObject hierarchyInfo = new JSONObject();
        for (ClientConnector connector : dirtyVisibleConnectors) {
            String connectorId = connector.getConnectorId();
            JSONArray children = new JSONArray();

            for (ClientConnector child : AbstractClientConnector
                    .getAllChildrenIterable(connector)) {
                if (LegacyCommunicationManager
                        .isConnectorVisibleToClient(child)) {
                    children.put(child.getConnectorId());
                }
            }
            try {
                hierarchyInfo.put(connectorId, children);
            } catch (JSONException e) {
                throw new PaintException(
                        "Failed to send hierarchy information about "
                                + connectorId + " to the client: "
                                + e.getMessage(), e);
            }
        }
        writer.write(hierarchyInfo.toString());
    }
}
