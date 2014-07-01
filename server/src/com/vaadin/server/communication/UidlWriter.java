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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.JsonPaintTarget;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.LegacyCommunicationManager.ClientCache;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;

/**
 * Serializes pending server-side changes to UI state to JSON. This includes
 * shared state, client RPC invocations, connector hierarchy changes, connector
 * type information among others.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class UidlWriter implements Serializable {

    /**
     * Writes a JSON object containing all pending changes to the given UI.
     * 
     * @param ui
     *            The {@link UI} whose changes to write
     * @param writer
     *            The writer to use
     * @param repaintAll
     *            Whether the client should re-render the whole UI.
     * @param analyzeLayouts
     *            Whether detected layout problems should be logged.
     * @param async
     *            True if this message is sent by the server asynchronously,
     *            false if it is a response to a client message.
     * 
     * @throws IOException
     *             If the writing fails.
     * @throws JSONException
     *             If the JSON serialization fails.
     */
    public void write(UI ui, Writer writer, boolean repaintAll, boolean async)
            throws IOException, JSONException {
        VaadinSession session = ui.getSession();
        VaadinService service = session.getService();

        // Purge pending access calls as they might produce additional changes
        // to write out
        service.runPendingAccessTasks(session);

        ArrayList<ClientConnector> dirtyVisibleConnectors = ui
                .getConnectorTracker().getDirtyVisibleConnectors();
        LegacyCommunicationManager manager = session.getCommunicationManager();
        // Paints components
        ConnectorTracker uiConnectorTracker = ui.getConnectorTracker();
        getLogger().log(Level.FINE, "* Creating response to client");

        getLogger().log(
                Level.FINE,
                "Found " + dirtyVisibleConnectors.size()
                        + " dirty connectors to paint");
        for (ClientConnector connector : dirtyVisibleConnectors) {
            boolean initialized = uiConnectorTracker
                    .isClientSideInitialized(connector);
            connector.beforeClientResponse(!initialized);
        }

        uiConnectorTracker.setWritingResponse(true);
        try {

            int syncId = service.getDeploymentConfiguration()
                    .isSyncIdCheckEnabled() ? uiConnectorTracker
                    .getCurrentSyncId() : -1;
            writer.write("\"" + ApplicationConstants.SERVER_SYNC_ID + "\": "
                    + syncId + ", ");

            writer.write("\"changes\" : ");

            JsonPaintTarget paintTarget = new JsonPaintTarget(manager, writer,
                    !repaintAll);

            new LegacyUidlWriter().write(ui, writer, paintTarget);

            paintTarget.close();
            writer.write(", "); // close changes

            // send shared state to client

            // for now, send the complete state of all modified and new
            // components

            // Ideally, all this would be sent before "changes", but that causes
            // complications with legacy components that create sub-components
            // in their paint phase. Nevertheless, this will be processed on the
            // client after component creation but before legacy UIDL
            // processing.

            writer.write("\"state\":");
            new SharedStateWriter().write(ui, writer);
            writer.write(", "); // close states

            // TODO This should be optimized. The type only needs to be
            // sent once for each connector id + on refresh. Use the same cache
            // as
            // widget mapping

            writer.write("\"types\":");
            new ConnectorTypeWriter().write(ui, writer, paintTarget);
            writer.write(", "); // close states

            // Send update hierarchy information to the client.

            // This could be optimized aswell to send only info if hierarchy has
            // actually changed. Much like with the shared state. Note though
            // that an empty hierarchy is information aswell (e.g. change from 1
            // child to 0 children)

            writer.write("\"hierarchy\":");
            new ConnectorHierarchyWriter().write(ui, writer);
            writer.write(", "); // close hierarchy

            // send server to client RPC calls for components in the UI, in call
            // order

            // collect RPC calls from components in the UI in the order in
            // which they were performed, remove the calls from components

            writer.write("\"rpc\" : ");
            new ClientRpcWriter().write(ui, writer);
            writer.write(", "); // close rpc

            uiConnectorTracker.markAllConnectorsClean();

            writer.write("\"meta\" : ");

            SystemMessages messages = ui.getSession().getService()
                    .getSystemMessages(ui.getLocale(), null);
            // TODO hilightedConnector
            new MetadataWriter().write(ui, writer, repaintAll, async, messages);
            writer.write(", ");

            writer.write("\"resources\" : ");
            new ResourceWriter().write(ui, writer, paintTarget);

            Collection<Class<? extends ClientConnector>> usedClientConnectors = paintTarget
                    .getUsedClientConnectors();
            boolean typeMappingsOpen = false;
            ClientCache clientCache = manager.getClientCache(ui);

            List<Class<? extends ClientConnector>> newConnectorTypes = new ArrayList<Class<? extends ClientConnector>>();

            for (Class<? extends ClientConnector> class1 : usedClientConnectors) {
                if (clientCache.cache(class1)) {
                    // client does not know the mapping key for this type, send
                    // mapping to client
                    newConnectorTypes.add(class1);

                    if (!typeMappingsOpen) {
                        typeMappingsOpen = true;
                        writer.write(", \"typeMappings\" : { ");
                    } else {
                        writer.write(" , ");
                    }
                    String canonicalName = class1.getCanonicalName();
                    writer.write("\"");
                    writer.write(canonicalName);
                    writer.write("\" : ");
                    writer.write(manager.getTagForType(class1));
                }
            }
            if (typeMappingsOpen) {
                writer.write(" }");
            }

            // TODO PUSH Refactor to TypeInheritanceWriter or something
            boolean typeInheritanceMapOpen = false;
            if (typeMappingsOpen) {
                // send the whole type inheritance map if any new mappings
                for (Class<? extends ClientConnector> class1 : usedClientConnectors) {
                    if (!ClientConnector.class.isAssignableFrom(class1
                            .getSuperclass())) {
                        continue;
                    }
                    if (!typeInheritanceMapOpen) {
                        typeInheritanceMapOpen = true;
                        writer.write(", \"typeInheritanceMap\" : { ");
                    } else {
                        writer.write(" , ");
                    }
                    writer.write("\"");
                    writer.write(manager.getTagForType(class1));
                    writer.write("\" : ");
                    writer.write(manager
                            .getTagForType((Class<? extends ClientConnector>) class1
                                    .getSuperclass()));
                }
                if (typeInheritanceMapOpen) {
                    writer.write(" }");
                }
            }

            // TODO Refactor to DependencyWriter or something
            /*
             * Ensure super classes come before sub classes to get script
             * dependency order right. Sub class @JavaScript might assume that
             * 
             * @JavaScript defined by super class is already loaded.
             */
            Collections.sort(newConnectorTypes, new Comparator<Class<?>>() {
                @Override
                public int compare(Class<?> o1, Class<?> o2) {
                    // TODO optimize using Class.isAssignableFrom?
                    return hierarchyDepth(o1) - hierarchyDepth(o2);
                }

                private int hierarchyDepth(Class<?> type) {
                    if (type == Object.class) {
                        return 0;
                    } else {
                        return hierarchyDepth(type.getSuperclass()) + 1;
                    }
                }
            });

            List<String> scriptDependencies = new ArrayList<String>();
            List<String> styleDependencies = new ArrayList<String>();

            for (Class<? extends ClientConnector> class1 : newConnectorTypes) {
                JavaScript jsAnnotation = class1
                        .getAnnotation(JavaScript.class);
                if (jsAnnotation != null) {
                    for (String uri : jsAnnotation.value()) {
                        scriptDependencies.add(manager.registerDependency(uri,
                                class1));
                    }
                }

                StyleSheet styleAnnotation = class1
                        .getAnnotation(StyleSheet.class);
                if (styleAnnotation != null) {
                    for (String uri : styleAnnotation.value()) {
                        styleDependencies.add(manager.registerDependency(uri,
                                class1));
                    }
                }
            }

            // Include script dependencies in output if there are any
            if (!scriptDependencies.isEmpty()) {
                writer.write(", \"scriptDependencies\": "
                        + new JSONArray(scriptDependencies).toString());
            }

            // Include style dependencies in output if there are any
            if (!styleDependencies.isEmpty()) {
                writer.write(", \"styleDependencies\": "
                        + new JSONArray(styleDependencies).toString());
            }

            session.getDragAndDropService().printJSONResponse(writer);

            for (ClientConnector connector : dirtyVisibleConnectors) {
                uiConnectorTracker.markClientSideInitialized(connector);
            }

            assert (uiConnectorTracker.getDirtyConnectors().isEmpty()) : "Connectors have been marked as dirty during the end of the paint phase. This is most certainly not intended.";

            writePerformanceData(ui, writer);
        } finally {
            uiConnectorTracker.setWritingResponse(false);
            uiConnectorTracker.cleanConnectorMap();
        }
    }

    /**
     * Adds the performance timing data (used by TestBench 3) to the UIDL
     * response.
     * 
     * @throws IOException
     */
    private void writePerformanceData(UI ui, Writer writer) throws IOException {
        writer.write(String.format(", \"timings\":[%d, %d]", ui.getSession()
                .getCumulativeRequestDuration(), ui.getSession()
                .getLastRequestDuration()));
    }

    private static final Logger getLogger() {
        return Logger.getLogger(UidlWriter.class.getName());
    }
}
