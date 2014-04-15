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

import org.json.JSONException;
import org.json.JSONObject;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.ui.UI;

/**
 * Serializes connector type mappings to JSON.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class ConnectorTypeWriter implements Serializable {

    /**
     * Writes a JSON object containing connector-ID-to-type-ID mappings for each
     * dirty Connector in the given UI.
     * 
     * @param ui
     *            The {@link UI} containing dirty connectors
     * @param writer
     *            The {@link Writer} used to write the JSON.
     * @param target
     *            The paint target containing the connector type IDs.
     * @throws IOException
     *             If the serialization fails.
     */
    public void write(UI ui, Writer writer, PaintTarget target)
            throws IOException {

        Collection<ClientConnector> dirtyVisibleConnectors = ui
                .getConnectorTracker().getDirtyVisibleConnectors();

        JSONObject connectorTypes = new JSONObject();
        for (ClientConnector connector : dirtyVisibleConnectors) {
            String connectorType = target.getTag(connector);
            try {
                connectorTypes.put(connector.getConnectorId(), connectorType);
            } catch (JSONException e) {
                throw new PaintException(
                        "Failed to send connector type for connector "
                                + connector.getConnectorId() + ": "
                                + e.getMessage(), e);
            }
        }
        writer.write(connectorTypes.toString());
    }
}
