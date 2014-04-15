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
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.server.ClientConnector;
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
import com.vaadin.shared.communication.LegacyChangeVariablesInvocation;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.communication.UidlValue;
import com.vaadin.ui.Component;
import com.vaadin.ui.ConnectorTracker;
import com.vaadin.ui.UI;

/**
 * Handles a client-to-server message containing serialized {@link ServerRpc
 * server RPC} invocations.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class ServerRpcHandler implements Serializable {

    /* Variable records indexes */
    public static final char VAR_BURST_SEPARATOR = '\u001d';

    public static final char VAR_ESCAPE_CHARACTER = '\u001b';

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
     * @throws JSONException
     *             If deserializing the JSON fails.
     */
    public void handleRpc(UI ui, Reader reader, VaadinRequest request)
            throws IOException, InvalidUIDLSecurityKeyException, JSONException {
        ui.getSession().setLastRequestTimestamp(System.currentTimeMillis());

        String changes = getMessage(reader);

        final String[] bursts = changes.split(String
                .valueOf(VAR_BURST_SEPARATOR));

        if (bursts.length > 2) {
            throw new RuntimeException(
                    "Multiple variable bursts not supported in Vaadin 7");
        } else if (bursts.length <= 1) {
            // The client sometimes sends empty messages, this is probably a bug
            return;
        }

        // Security: double cookie submission pattern unless disabled by
        // property
        if (!VaadinService.isCsrfTokenValid(ui.getSession(), bursts[0])) {
            throw new InvalidUIDLSecurityKeyException("");
        }
        handleBurst(ui, unescapeBurst(bursts[1]));
    }

    /**
     * Processes a message burst received from the client.
     * 
     * A burst can contain any number of RPC calls, including legacy variable
     * change calls that are processed separately.
     * 
     * Consecutive changes to the value of the same variable are combined and
     * changeVariables() is only called once for them. This preserves the Vaadin
     * 6 semantics for components and add-ons that do not use Vaadin 7 RPC
     * directly.
     * 
     * @param source
     * @param uI
     *            the UI receiving the burst
     * @param burst
     *            the content of the burst as a String to be parsed
     */
    private void handleBurst(UI uI, String burst) {
        // TODO PUSH Refactor so that this is not needed
        LegacyCommunicationManager manager = uI.getSession()
                .getCommunicationManager();

        try {
            Set<Connector> enabledConnectors = new HashSet<Connector>();

            List<MethodInvocation> invocations = parseInvocations(
                    uI.getConnectorTracker(), burst);
            for (MethodInvocation invocation : invocations) {
                final ClientConnector connector = manager.getConnector(uI,
                        invocation.getConnectorId());

                if (connector != null && connector.isConnectorEnabled()) {
                    enabledConnectors.add(connector);
                }
            }

            for (int i = 0; i < invocations.size(); i++) {
                MethodInvocation invocation = invocations.get(i);

                final ClientConnector connector = manager.getConnector(uI,
                        invocation.getConnectorId());
                if (connector == null) {
                    getLogger()
                            .log(Level.WARNING,
                                    "Received RPC call for unknown connector with id {0} (tried to invoke {1}.{2})",
                                    new Object[] { invocation.getConnectorId(),
                                            invocation.getInterfaceName(),
                                            invocation.getMethodName() });
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
                    }

                    // Connector is disabled, log a warning and move to the next
                    getLogger().warning(
                            getIgnoredDisabledError("RPC call", connector));
                    continue;
                }
                // DragAndDropService has null UI
                if (connector.getUI() != null && connector.getUI().isClosing()) {
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
                    try {
                        ServerRpcManager.applyInvocation(connector,
                                (ServerRpcMethodInvocation) invocation);
                    } catch (RpcInvocationException e) {
                        manager.handleConnectorRelatedException(connector, e);
                    }
                } else {

                    // All code below is for legacy variable changes
                    LegacyChangeVariablesInvocation legacyInvocation = (LegacyChangeVariablesInvocation) invocation;
                    Map<String, Object> changes = legacyInvocation
                            .getVariableChanges();
                    try {
                        if (connector instanceof VariableOwner) {
                            // The source parameter is never used anywhere
                            changeVariables(null, (VariableOwner) connector,
                                    changes);
                        } else {
                            throw new IllegalStateException(
                                    "Received legacy variable change for "
                                            + connector.getClass().getName()
                                            + " ("
                                            + connector.getConnectorId()
                                            + ") which is not a VariableOwner. The client-side connector sent these legacy varaibles: "
                                            + changes.keySet());
                        }
                    } catch (Exception e) {
                        manager.handleConnectorRelatedException(connector, e);
                    }
                }
            }
        } catch (JSONException e) {
            getLogger().warning(
                    "Unable to parse RPC call from the client: "
                            + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse a message burst from the client into a list of MethodInvocation
     * instances.
     * 
     * @param connectorTracker
     *            The ConnectorTracker used to lookup connectors
     * @param burst
     *            message string (JSON)
     * @return list of MethodInvocation to perform
     * @throws JSONException
     */
    private List<MethodInvocation> parseInvocations(
            ConnectorTracker connectorTracker, String burst)
            throws JSONException {
        JSONArray invocationsJson = new JSONArray(burst);

        ArrayList<MethodInvocation> invocations = new ArrayList<MethodInvocation>();

        MethodInvocation previousInvocation = null;
        // parse JSON to MethodInvocations
        for (int i = 0; i < invocationsJson.length(); ++i) {

            JSONArray invocationJson = invocationsJson.getJSONArray(i);

            MethodInvocation invocation = parseInvocation(invocationJson,
                    previousInvocation, connectorTracker);
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

    private MethodInvocation parseInvocation(JSONArray invocationJson,
            MethodInvocation previousInvocation,
            ConnectorTracker connectorTracker) throws JSONException {
        String connectorId = invocationJson.getString(0);
        String interfaceName = invocationJson.getString(1);
        String methodName = invocationJson.getString(2);

        if (connectorTracker.getConnector(connectorId) == null
                && !connectorId
                        .equals(ApplicationConstants.DRAG_AND_DROP_CONNECTOR_ID)) {
            getLogger()
                    .log(Level.WARNING,
                            "RPC call to "
                                    + interfaceName
                                    + "."
                                    + methodName
                                    + " received for connector "
                                    + connectorId
                                    + " but no such connector could be found. Resynchronizing client.");
            // This is likely an out of sync issue (client tries to update a
            // connector which is not present). Force resync.
            connectorTracker.markAllConnectorsDirty();
            return null;
        }

        JSONArray parametersJson = invocationJson.getJSONArray(3);

        if (LegacyChangeVariablesInvocation.isLegacyVariableChange(
                interfaceName, methodName)) {
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
            JSONArray parametersJson, ConnectorTracker connectorTracker)
            throws JSONException {
        if (parametersJson.length() != 2) {
            throw new JSONException(
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
            JSONArray parametersJson, ConnectorTracker connectorTracker)
            throws JSONException {
        ClientConnector connector = connectorTracker.getConnector(connectorId);

        ServerRpcManager<?> rpcManager = connector.getRpcManager(interfaceName);
        if (rpcManager == null) {
            /*
             * Security: Don't even decode the json parameters if no RpcManager
             * corresponding to the received method invocation has been
             * registered.
             */
            getLogger().warning(
                    "Ignoring RPC call to " + interfaceName + "." + methodName
                            + " in connector " + connector.getClass().getName()
                            + "(" + connectorId
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
            Object parameterValue = parametersJson.get(j);
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

    /**
     * Unescape encoded burst separator characters in a burst received from the
     * client. This protects from separator injection attacks.
     * 
     * @param encodedValue
     *            to decode
     * @return decoded value
     */
    protected String unescapeBurst(String encodedValue) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(
                encodedValue);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (VAR_ESCAPE_CHARACTER == character) {
                character = iterator.next();
                switch (character) {
                case VAR_ESCAPE_CHARACTER + 0x30:
                    // escaped escape character
                    result.append(VAR_ESCAPE_CHARACTER);
                    break;
                case VAR_BURST_SEPARATOR + 0x30:
                    // +0x30 makes these letters for easier reading
                    result.append((char) (character - 0x30));
                    break;
                case CharacterIterator.DONE:
                    // error
                    throw new RuntimeException(
                            "Communication error: Unexpected end of message");
                default:
                    // other escaped character - probably a client-server
                    // version mismatch
                    throw new RuntimeException(
                            "Invalid escaped character from the client - check that the widgetset and server versions match");
                }
            } else {
                // not a special character - add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
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
