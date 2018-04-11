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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.DependencyFilter.FilterContext;
import com.vaadin.server.JsonPaintTarget;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.LegacyCommunicationManager.ClientCache;
import com.vaadin.server.SystemMessages;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.Dependency;
import com.vaadin.ui.UI;

import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.impl.JsonUtil;

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
     * @param async
     *            True if this message is sent by the server asynchronously,
     *            false if it is a response to a client message.
     *
     * @throws IOException
     *             If the writing fails.
     */
    public void write(UI ui, Writer writer, boolean async) throws IOException {
        VaadinSession session = ui.getSession();
        VaadinService service = session.getService();

        // Purge pending access calls as they might produce additional changes
        // to write out
        service.runPendingAccessTasks(session);

        Set<ClientConnector> processedConnectors = new HashSet<>();

        LegacyCommunicationManager manager = session.getCommunicationManager();
        ClientCache clientCache = manager.getClientCache(ui);
        boolean repaintAll = clientCache.isEmpty();
        // Paints components
        ConnectorTracker uiConnectorTracker = ui.getConnectorTracker();
        getLogger().log(Level.FINE, "* Creating response to client");

        while (true) {
            List<ClientConnector> connectorsToProcess = new ArrayList<>();
            for (ClientConnector c : uiConnectorTracker
                    .getDirtyVisibleConnectors()) {
                if (!processedConnectors.contains(c)) {
                    connectorsToProcess.add(c);
                }
            }

            if (connectorsToProcess.isEmpty()) {
                break;
            }

            // process parents before children
            Collections.sort(connectorsToProcess,
                    Comparator.comparingInt(conn -> {
                        int depth = 0;
                        ClientConnector connector = conn;
                        // this is a very fast operation, even for 100+ levels
                        while (connector.getParent() != null) {
                            ++depth;
                            connector = connector.getParent();
                        }
                        return depth;
                    }));

            for (ClientConnector connector : connectorsToProcess) {
                // call isDirty() to find out if ConnectorTracker knows the
                // connector
                boolean initialized = uiConnectorTracker.isDirty(connector)
                        && uiConnectorTracker
                                .isClientSideInitialized(connector);
                processedConnectors.add(connector);

                try {
                    connector.beforeClientResponse(!initialized);
                } catch (RuntimeException e) {
                    manager.handleConnectorRelatedException(connector, e);
                }
            }
        }

        getLogger().log(Level.FINE, "Found " + processedConnectors.size()
                + " dirty connectors to paint");

        uiConnectorTracker.setWritingResponse(true);
        try {

            int syncId = service.getDeploymentConfiguration()
                    .isSyncIdCheckEnabled()
                            ? uiConnectorTracker.getCurrentSyncId()
                            : -1;
            writer.write("\"" + ApplicationConstants.SERVER_SYNC_ID + "\": "
                    + syncId + ", ");
            if (repaintAll) {
                writer.write("\"" + ApplicationConstants.RESYNCHRONIZE_ID
                        + "\": true, ");
            }
            int nextClientToServerMessageId = ui
                    .getLastProcessedClientToServerId() + 1;
            writer.write("\"" + ApplicationConstants.CLIENT_TO_SERVER_ID
                    + "\": " + nextClientToServerMessageId + ", ");
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
            Set<String> stateUpdateConnectors = new SharedStateWriter()
                    .write(ui, writer);
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
            new ConnectorHierarchyWriter().write(ui, writer,
                    stateUpdateConnectors);
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

            List<Class<? extends ClientConnector>> newConnectorTypes = new ArrayList<>();

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
                    if (!ClientConnector.class
                            .isAssignableFrom(class1.getSuperclass())) {
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
                    writer.write(manager.getTagForType(
                            (Class<? extends ClientConnector>) class1
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

            List<Dependency> dependencies = new ArrayList<>();
            dependencies.addAll(ui.getPage().getPendingDependencies());
            dependencies.addAll(Dependency.findDependencies(newConnectorTypes,
                    manager, new FilterContext(session)));

            // Include dependencies in output if there are any
            if (!dependencies.isEmpty()) {
                writer.write(", \"dependencies\": "
                        + JsonUtil.stringify(toJsonArray(dependencies)));
            }

            session.getDragAndDropService().printJSONResponse(writer);

            for (ClientConnector connector : processedConnectors) {
                uiConnectorTracker.markClientSideInitialized(connector);
            }

            assert (uiConnectorTracker.getDirtyConnectors()
                    .isEmpty()) : "Connectors have been marked as dirty during the end of the paint phase. This is most certainly not intended.";

            writePerformanceData(ui, writer);
        } finally {
            uiConnectorTracker.setWritingResponse(false);
            uiConnectorTracker.cleanConnectorMap(true);
        }
    }

    private JsonArray toJsonArray(List<Dependency> list) {
        JsonArray result = Json.createArray();
        for (int i = 0; i < list.size(); i++) {
            JsonObject dep = Json.createObject();
            Dependency dependency = list.get(i);
            dep.put("type", dependency.getType().name());
            dep.put("url", dependency.getUrl());
            result.set(i, dep);
        }

        return result;
    }

    /**
     * Adds the performance timing data (used by TestBench 3) to the UIDL
     * response.
     *
     * @throws IOException
     */
    private void writePerformanceData(UI ui, Writer writer) throws IOException {
        if (!ui.getSession().getService().getDeploymentConfiguration()
                .isProductionMode()) {
            writer.write(String.format(", \"timings\":[%d, %d]",
                    ui.getSession().getCumulativeRequestDuration(),
                    ui.getSession().getLastRequestDuration()));
        }
    }

    private static final Logger getLogger() {
        return Logger.getLogger(UidlWriter.class.getName());
    }
}
