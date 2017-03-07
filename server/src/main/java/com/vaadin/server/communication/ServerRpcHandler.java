/*
 * Copyright 2000-2016 Vaadin Ltd.
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
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.Constants;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.LegacyCommunicationManager;
import com.vaadin.server.LegacyCommunicationManager.InvalidUIDLSecurityKeyException;
import com.vaadin.server.ServerRpcManager;
import com.vaadin.server.ServerRpcManager.RpcInvocationException;
import com.vaadin.server.ServerRpcMethodInvocation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VariableOwner;
import com.vaadin.shared.ApplicationConstants;
import com.vaadin.shared.Connector;
import com.vaadin.shared.Version;
import com.vaadin.shared.communication.LegacyChangeVariablesInvocation;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.communication.UidlValue;
import com.vaadin.shared.data.DataRequestRpc;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;

import elemental.json.JsonArray;
import elemental.json.JsonException;
import elemental.json.JsonObject;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;

/**
 * Handles a client-to-server message containing serialized {@link ServerRpc
 * server RPC} invocations.
 *
 * @author Vaadin Ltd
 * @since 7.1
 */
public class ServerRpcHandler implements Serializable {

    /**
     * A data transfer object representing an RPC request sent by the client
     * side.
     *
     * @since 7.2
     * @author Vaadin Ltd
     */
    public static class RpcRequest implements Serializable {

        private final String csrfToken;
        private final JsonArray invocations;
        private final int syncId;
        private final JsonObject json;
        private final boolean resynchronize;
        private final int clientToServerMessageId;
        private String widgetsetVersion = null;

        public RpcRequest(String jsonString, VaadinRequest request) {
            json = JsonUtil.parse(jsonString);

            JsonValue token = json.get(ApplicationConstants.CSRF_TOKEN);
            if (token == null) {
                csrfToken = ApplicationConstants.CSRF_TOKEN_DEFAULT_VALUE;
            } else {
                String csrfToken = token.asString();
                if (csrfToken.isEmpty()) {
                    csrfToken = ApplicationConstants.CSRF_TOKEN_DEFAULT_VALUE;
                }
                this.csrfToken = csrfToken;
            }

            if (request.getService().getDeploymentConfiguration()
                    .isSyncIdCheckEnabled()) {
                syncId = (int) json
                        .getNumber(ApplicationConstants.SERVER_SYNC_ID);
            } else {
                syncId = -1;
            }

            if (json.hasKey(ApplicationConstants.RESYNCHRONIZE_ID)) {
                resynchronize = json
                        .getBoolean(ApplicationConstants.RESYNCHRONIZE_ID);
            } else {
                resynchronize = false;
            }
            if (json.hasKey(ApplicationConstants.WIDGETSET_VERSION_ID)) {
                widgetsetVersion = json
                        .getString(ApplicationConstants.WIDGETSET_VERSION_ID);
            }

            if (json.hasKey(ApplicationConstants.CLIENT_TO_SERVER_ID)) {
                clientToServerMessageId = (int) json
                        .getNumber(ApplicationConstants.CLIENT_TO_SERVER_ID);
            } else {
                getLogger()
                        .warning("Server message without client id received");
                clientToServerMessageId = -1;
            }
            invocations = json.getArray(ApplicationConstants.RPC_INVOCATIONS);
        }

        /**
         * Gets the CSRF security token (double submit cookie) for this request.
         *
         * @return the CSRF security token for this current change request
         */
        public String getCsrfToken() {
            return csrfToken;
        }

        /**
         * Gets the data to recreate the RPC as requested by the client side.
         *
         * @return the data describing which RPC should be made, and all their
         *         data
         */
        public JsonArray getRpcInvocationsData() {
            return invocations;
        }

        /**
         * Gets the sync id last seen by the client.
         *
         * @return the last sync id given by the server, according to the
         *         client's request
         */
        public int getSyncId() {
            return syncId;
        }

        /**
         * Checks if this is a request to resynchronize the client side
         *
         * @return true if this is a resynchronization request, false otherwise
         */
        public boolean isResynchronize() {
            return resynchronize;
        }

        /**
         * Gets the id of the client to server message
         *
         * @since 7.6
         * @return the server message id
         */
        public int getClientToServerId() {
            return clientToServerMessageId;
        }

        /**
         * Gets the entire request in JSON format, as it was received from the
         * client.
         * <p>
         * <em>Note:</em> This is a shared reference - any modifications made
         * will be shared.
         *
         * @return the raw JSON object that was received from the client
         *
         */
        public JsonObject getRawJson() {
            return json;
        }

        /**
         * Gets the widget set version reported by the client
         *
         * @since 7.6
         * @return The widget set version reported by the client or null if the
         *         message did not contain a widget set version
         */
        public String getWidgetsetVersion() {
            return widgetsetVersion;
        }
    }

    private static final int MAX_BUFFER_SIZE = 64 * 1024;

    /**
     * Reads JSON containing zero or more serialized RPC calls (including legacy
     * variable changes) and executes the calls.
     *
     * @param ui
     *            The {@link UI} receiving the calls. Cannot be null.
     * @param reader
     *            The {@link Reader} used to read the JSON.
     * @param request
     * @throws IOException
     *             If reading the message fails.
     * @throws InvalidUIDLSecurityKeyException
     *             If the received security key does not match the one stored in
     *             the session.
     */
    public void handleRpc(UI ui, Reader reader, VaadinRequest request)
            throws IOException, InvalidUIDLSecurityKeyException {
        ui.getSession().setLastRequestTimestamp(System.currentTimeMillis());

        String changeMessage = getMessage(reader);

        if (changeMessage == null || changeMessage.isEmpty()) {
            // The client sometimes sends empty messages, this is probably a bug
            return;
        }

        RpcRequest rpcRequest = new RpcRequest(changeMessage, request);

        // Security: double cookie submission pattern unless disabled by
        // property
        if (!VaadinService.isCsrfTokenValid(ui.getSession(),
                rpcRequest.getCsrfToken())) {
            throw new InvalidUIDLSecurityKeyException("");
        }

        checkWidgetsetVersion(rpcRequest.getWidgetsetVersion());

        int expectedId = ui.getLastProcessedClientToServerId() + 1;
        if (rpcRequest.getClientToServerId() != -1
                && rpcRequest.getClientToServerId() != expectedId) {
            // Invalid message id, skip RPC processing but force a full
            // re-synchronization of the client as it might have not received
            // the previous response (e.g. due to a bad connection)

            // Must resync also for duplicate messages because the server might
            // have generated a response for the first message but the response
            // did not reach the client. When the client re-sends the message,
            // it would only get an empty response (because the dirty flags have
            // been cleared on the server) and would be out of sync
            ui.getSession().getCommunicationManager().repaintAll(ui);

            if (rpcRequest.getClientToServerId() < expectedId) {
                // Just a duplicate message due to a bad connection or similar
                // It has already been handled by the server so it is safe to
                // ignore
                getLogger()
                        .fine("Ignoring old message from the client. Expected: "
                                + expectedId + ", got: "
                                + rpcRequest.getClientToServerId());
            } else {
                getLogger().warning(
                        "Unexpected message id from the client. Expected: "
                                + expectedId + ", got: "
                                + rpcRequest.getClientToServerId());
            }
        } else {
            // Message id ok, process RPCs
            ui.setLastProcessedClientToServerId(expectedId);
            handleInvocations(ui, rpcRequest.getSyncId(),
                    rpcRequest.getRpcInvocationsData());
        }

        if (rpcRequest.isResynchronize()) {
            ui.getSession().getCommunicationManager().repaintAll(ui);
        }

    }

    /**
     * Checks that the version reported by the client (widgetset) matches that
     * of the server.
     *
     * @param widgetsetVersion
     *            the widget set version reported by the client or null
     */
    private void checkWidgetsetVersion(String widgetsetVersion) {
        if (widgetsetVersion == null) {
            // Only check when the widgetset version is reported. It is reported
            // in the first UIDL request (not the initial request as it is a
            // plain GET /)
            return;
        }

        if (!Version.getFullVersion().equals(widgetsetVersion)) {
            getLogger().warning(String.format(Constants.WIDGETSET_MISMATCH_INFO,
                    Version.getFullVersion(), widgetsetVersion));
        }
    }

    /**
     * Processes invocations data received from the client.
     * <p>
     * The invocations data can contain any number of RPC calls, including
     * legacy variable change calls that are processed separately.
     * <p>
     * Consecutive changes to the value of the same variable are combined and
     * changeVariables() is only called once for them. This preserves the Vaadin
     * 6 semantics for components and add-ons that do not use Vaadin 7 RPC
     * directly.
     *
     * @param ui
     *            the UI receiving the invocations data
     * @param lastSyncIdSeenByClient
     *            the most recent sync id the client has seen at the time the
     *            request was sent
     * @param invocationsData
     *            JSON containing all information needed to execute all
     *            requested RPC calls.
     * @since 7.7
     */
    protected void handleInvocations(UI ui, int lastSyncIdSeenByClient,
            JsonArray invocationsData) {
        // TODO PUSH Refactor so that this is not needed
        LegacyCommunicationManager manager = ui.getSession()
                .getCommunicationManager();

        try {
            ConnectorTracker connectorTracker = ui.getConnectorTracker();

            Set<Connector> enabledConnectors = new HashSet<>();

            List<MethodInvocation> invocations = parseInvocations(
                    ui.getConnectorTracker(), invocationsData,
                    lastSyncIdSeenByClient);
            for (MethodInvocation invocation : invocations) {
                final ClientConnector connector = connectorTracker
                        .getConnector(invocation.getConnectorId());

                if (connector != null && connector.isConnectorEnabled()) {
                    enabledConnectors.add(connector);
                }
            }

            for (MethodInvocation invocation : invocations) {
                final ClientConnector connector = connectorTracker
                        .getConnector(invocation.getConnectorId());
                if (connector == null) {
                    logUnknownConnector(invocation.getConnectorId(),
                            invocation.getInterfaceName(),
                            invocation.getMethodName());
                    continue;
                }

                if (!enabledConnectors.contains(connector)) {

                    if (invocation instanceof LegacyChangeVariablesInvocation) {
                        LegacyChangeVariablesInvocation legacyInvocation = (LegacyChangeVariablesInvocation) invocation;
                        // TODO convert window close to a separate RPC call and
                        // handle above - not a variable change

                        // Handle special case where window-close is called
                        // after the window has been removed from the
                        // application or the application has closed
                        Map<String, Object> changes = legacyInvocation
                                .getVariableChanges();
                        if (changes.size() == 1 && changes.containsKey("close")
                                && Boolean.TRUE.equals(changes.get("close"))) {
                            // Silently ignore this
                            continue;
                        }
                    } else if (invocation instanceof ServerRpcMethodInvocation) {
                        ServerRpcMethodInvocation rpc = (ServerRpcMethodInvocation) invocation;
                        // special case for data communicator requesting more
                        // data
                        if (DataRequestRpc.class.getName()
                                .equals(rpc.getInterfaceClass().getName())) {
                            handleInvocation(ui, connector, rpc);
                        }
                        continue;
                    }

                    // Connector is disabled, log a warning and move to the next
                    getLogger().warning(
                            getIgnoredDisabledError("RPC call", connector));
                    continue;
                }
                // DragAndDropService has null UI
                if (connector.getUI() != null
                        && connector.getUI().isClosing()) {
                    String msg = "Ignoring RPC call for connector "
                            + connector.getClass().getName();
                    if (connector instanceof Component) {
                        String caption = ((Component) connector).getCaption();
                        if (caption != null) {
                            msg += ", caption=" + caption;
                        }
                    }
                    msg += " in closed UI";
                    getLogger().warning(msg);
                    continue;

                }

                if (invocation instanceof ServerRpcMethodInvocation) {
                    handleInvocation(ui, connector,
                            (ServerRpcMethodInvocation) invocation);
                } else {
                    LegacyChangeVariablesInvocation legacyInvocation = (LegacyChangeVariablesInvocation) invocation;
                    handleInvocation(ui, connector, legacyInvocation);
                }
            }
        } catch (JsonException e) {
            getLogger().warning("Unable to parse RPC call from the client: "
                    + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void logUnknownConnector(String connectorId, String interfaceName,
            String methodName) {
        getLogger().log(Level.FINE,
                "Received RPC call for unknown connector with id {0} (tried to invoke {1}.{2})",
                new Object[] { connectorId, interfaceName, methodName });
    }

    /**
     * Handles the given RPC method invocation for the given connector
     *
     * @since 7.7
     * @param ui
     *            the UI containing the connector
     * @param connector
     *            the connector the RPC is targeted to
     * @param invocation
     *            information about the rpc to invoke
     */
    protected void handleInvocation(UI ui, ClientConnector connector,
            ServerRpcMethodInvocation invocation) {
        try {
            ServerRpcManager.applyInvocation(connector, invocation);
        } catch (RpcInvocationException e) {
            ui.getSession().getCommunicationManager()
                    .handleConnectorRelatedException(connector, e);
        }

    }

    /**
     * Handles the given Legacy variable change RPC method invocation for the
     * given connector
     *
     * @since 7.7
     * @param ui
     *            the UI containing the connector
     * @param connector
     *            the connector the RPC is targeted to
     * @param legacyInvocation
     *            information about the rpc to invoke
     */
    protected void handleInvocation(UI ui, ClientConnector connector,
            LegacyChangeVariablesInvocation legacyInvocation) {
        Map<String, Object> changes = legacyInvocation.getVariableChanges();
        try {
            if (connector instanceof VariableOwner) {
                // The source parameter is never used anywhere
                changeVariables(null, (VariableOwner) connector, changes);
            } else {
                throw new IllegalStateException(
                        "Received a legacy variable change for "
                                + connector.getClass().getName() + " ("
                                + connector.getConnectorId()
                                + ") which is not a VariableOwner. The client-side connector sent these legacy variables: "
                                + changes.keySet());
            }
        } catch (Exception e) {
            ui.getSession().getCommunicationManager()
                    .handleConnectorRelatedException(connector, e);
        }

    }

    /**
     * Parse JSON from the client into a list of MethodInvocation instances.
     *
     * @param connectorTracker
     *            The ConnectorTracker used to lookup connectors
     * @param invocationsJson
     *            JSON containing all information needed to execute all
     *            requested RPC calls.
     * @param lastSyncIdSeenByClient
     *            the most recent sync id the client has seen at the time the
     *            request was sent
     * @return list of MethodInvocation to perform
     */
    private List<MethodInvocation> parseInvocations(
            ConnectorTracker connectorTracker, JsonArray invocationsJson,
            int lastSyncIdSeenByClient) {
        int invocationCount = invocationsJson.length();
        ArrayList<MethodInvocation> invocations = new ArrayList<>(
                invocationCount);

        MethodInvocation previousInvocation = null;
        // parse JSON to MethodInvocations
        for (int i = 0; i < invocationCount; ++i) {

            JsonArray invocationJson = invocationsJson.getArray(i);

            MethodInvocation invocation = parseInvocation(invocationJson,
                    previousInvocation, connectorTracker,
                    lastSyncIdSeenByClient);
            if (invocation != null) {
                // Can be null if the invocation was a legacy invocation and it
                // was merged with the previous one or if the invocation was
                // rejected because of an error.
                invocations.add(invocation);
                previousInvocation = invocation;
            }
        }
        return invocations;
    }

    private MethodInvocation parseInvocation(JsonArray invocationJson,
            MethodInvocation previousInvocation,
            ConnectorTracker connectorTracker, long lastSyncIdSeenByClient) {
        String connectorId = invocationJson.getString(0);
        String interfaceName = invocationJson.getString(1);
        String methodName = invocationJson.getString(2);

        JsonArray parametersJson = invocationJson.getArray(3);

        if (LegacyChangeVariablesInvocation
                .isLegacyVariableChange(interfaceName, methodName)) {
            if (!(previousInvocation instanceof LegacyChangeVariablesInvocation)) {
                previousInvocation = null;
            }

            return parseLegacyChangeVariablesInvocation(connectorId,
                    interfaceName, methodName,
                    (LegacyChangeVariablesInvocation) previousInvocation,
                    parametersJson, connectorTracker);
        } else {
            return parseServerRpcInvocation(connectorId, interfaceName,
                    methodName, parametersJson, connectorTracker);
        }

    }

    private LegacyChangeVariablesInvocation parseLegacyChangeVariablesInvocation(
            String connectorId, String interfaceName, String methodName,
            LegacyChangeVariablesInvocation previousInvocation,
            JsonArray parametersJson, ConnectorTracker connectorTracker) {
        if (parametersJson.length() != 2) {
            throw new JsonException(
                    "Invalid parameters in legacy change variables call. Expected 2, was "
                            + parametersJson.length());
        }
        String variableName = parametersJson.getString(0);
        UidlValue uidlValue = (UidlValue) JsonCodec.decodeInternalType(
                UidlValue.class, true, parametersJson.get(1), connectorTracker);

        Object value = uidlValue.getValue();

        if (previousInvocation != null
                && previousInvocation.getConnectorId().equals(connectorId)) {
            previousInvocation.setVariableChange(variableName, value);
            return null;
        } else {
            return new LegacyChangeVariablesInvocation(connectorId,
                    variableName, value);
        }
    }

    private ServerRpcMethodInvocation parseServerRpcInvocation(
            String connectorId, String interfaceName, String methodName,
            JsonArray parametersJson, ConnectorTracker connectorTracker)
            throws JsonException {
        ClientConnector connector = connectorTracker.getConnector(connectorId);
        if (connector == null) {
            logUnknownConnector(connectorId, interfaceName, methodName);
            return null;
        }
        ServerRpcManager<?> rpcManager = connector.getRpcManager(interfaceName);
        if (rpcManager == null) {
            /*
             * Security: Don't even decode the json parameters if no RpcManager
             * corresponding to the received method invocation has been
             * registered.
             */
            getLogger().warning("Ignoring RPC call to " + interfaceName + "."
                    + methodName + " in connector "
                    + connector.getClass().getName() + "(" + connectorId
                    + ") as no RPC implementation is registered");
            return null;
        }

        // Use interface from RpcManager instead of loading the class based on
        // the string name to avoid problems with OSGi
        Class<? extends ServerRpc> rpcInterface = rpcManager.getRpcInterface();

        ServerRpcMethodInvocation invocation = new ServerRpcMethodInvocation(
                connectorId, rpcInterface, methodName, parametersJson.length());

        Object[] parameters = new Object[parametersJson.length()];
        Type[] declaredRpcMethodParameterTypes = invocation.getMethod()
                .getGenericParameterTypes();

        for (int j = 0; j < parametersJson.length(); ++j) {
            JsonValue parameterValue = parametersJson.get(j);
            Type parameterType = declaredRpcMethodParameterTypes[j];
            parameters[j] = JsonCodec.decodeInternalOrCustomType(parameterType,
                    parameterValue, connectorTracker);
        }
        invocation.setParameters(parameters);
        return invocation;
    }

    protected void changeVariables(Object source, VariableOwner owner,
            Map<String, Object> m) {
        owner.changeVariables(source, m);
    }

    protected String getMessage(Reader reader) throws IOException {

        StringBuilder sb = new StringBuilder(MAX_BUFFER_SIZE);
        char[] buffer = new char[MAX_BUFFER_SIZE];

        while (true) {
            int read = reader.read(buffer);
            if (read == -1) {
                break;
            }
            sb.append(buffer, 0, read);
        }

        return sb.toString();
    }

    private static final Logger getLogger() {
        return Logger.getLogger(ServerRpcHandler.class.getName());
    }

    /**
     * Generates an error message when the client is trying to to something
     * ('what') with a connector which is disabled or invisible.
     *
     * @since 7.1.8
     * @param connector
     *            the connector which is disabled (or invisible)
     * @return an error message
     */
    public static String getIgnoredDisabledError(String what,
            ClientConnector connector) {
        String msg = "Ignoring " + what + " for disabled connector "
                + connector.getClass().getName();
        if (connector instanceof Component) {
            String caption = ((Component) connector).getCaption();
            if (caption != null) {
                msg += ", caption=" + caption;
            }
        }
        return msg;
    }
}
