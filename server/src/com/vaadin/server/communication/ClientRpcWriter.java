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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.ClientMethodInvocation;
import com.vaadin.server.EncodeResult;
import com.vaadin.server.JsonCodec;
import com.vaadin.server.PaintException;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.ui.UI;

/**
 * Serializes {@link ClientRpc client RPC} invocations to JSON.
 * 
 * @author Vaadin Ltd
 * @since 7.1
 */
public class ClientRpcWriter implements Serializable {

    /**
     * Writes a JSON object containing all pending client RPC invocations in the
     * given UI.
     * 
     * @param ui
     *            The {@link UI} whose RPC calls to write.
     * @param writer
     *            The {@link Writer} used to write the JSON.
     * @throws IOException
     *             If the serialization fails.
     */
    public void write(UI ui, Writer writer) throws IOException {

        Collection<ClientMethodInvocation> pendingInvocations = collectPendingRpcCalls(ui
                .getConnectorTracker().getDirtyVisibleConnectors());

        JSONArray rpcCalls = new JSONArray();
        for (ClientMethodInvocation invocation : pendingInvocations) {
            // add invocation to rpcCalls
            try {
                JSONArray invocationJson = new JSONArray();
                invocationJson.put(invocation.getConnector().getConnectorId());
                invocationJson.put(invocation.getInterfaceName());
                invocationJson.put(invocation.getMethodName());
                JSONArray paramJson = new JSONArray();
                for (int i = 0; i < invocation.getParameterTypes().length; ++i) {
                    Type parameterType = invocation.getParameterTypes()[i];
                    Object referenceParameter = null;
                    // TODO Use default values for RPC parameter types
                    // if (!JsonCodec.isInternalType(parameterType)) {
                    // try {
                    // referenceParameter = parameterType.newInstance();
                    // } catch (Exception e) {
                    // logger.log(Level.WARNING,
                    // "Error creating reference object for parameter of type "
                    // + parameterType.getName());
                    // }
                    // }
                    EncodeResult encodeResult = JsonCodec.encode(
                            invocation.getParameters()[i], referenceParameter,
                            parameterType, ui.getConnectorTracker());
                    paramJson.put(encodeResult.getEncodedValue());
                }
                invocationJson.put(paramJson);
                rpcCalls.put(invocationJson);
            } catch (JSONException e) {
                throw new PaintException(
                        "Failed to serialize RPC method call parameters for connector "
                                + invocation.getConnector().getConnectorId()
                                + " method " + invocation.getInterfaceName()
                                + "." + invocation.getMethodName() + ": "
                                + e.getMessage(), e);
            }
        }
        writer.write(rpcCalls.toString());
    }

    /**
     * Collects all pending RPC calls from listed {@link ClientConnector}s and
     * clears their RPC queues.
     * 
     * @param rpcPendingQueue
     *            list of {@link ClientConnector} of interest
     * @return ordered list of pending RPC calls
     */
    private Collection<ClientMethodInvocation> collectPendingRpcCalls(
            Collection<ClientConnector> rpcPendingQueue) {
        List<ClientMethodInvocation> pendingInvocations = new ArrayList<ClientMethodInvocation>();
        for (ClientConnector connector : rpcPendingQueue) {
            List<ClientMethodInvocation> paintablePendingRpc = connector
                    .retrievePendingRpcCalls();
            if (null != paintablePendingRpc && !paintablePendingRpc.isEmpty()) {
                List<ClientMethodInvocation> oldPendingRpc = pendingInvocations;
                int totalCalls = pendingInvocations.size()
                        + paintablePendingRpc.size();
                pendingInvocations = new ArrayList<ClientMethodInvocation>(
                        totalCalls);

                // merge two ordered comparable lists
                for (int destIndex = 0, oldIndex = 0, paintableIndex = 0; destIndex < totalCalls; destIndex++) {
                    if (paintableIndex >= paintablePendingRpc.size()
                            || (oldIndex < oldPendingRpc.size() && ((Comparable<ClientMethodInvocation>) oldPendingRpc
                                    .get(oldIndex))
                                    .compareTo(paintablePendingRpc
                                            .get(paintableIndex)) <= 0)) {
                        pendingInvocations.add(oldPendingRpc.get(oldIndex++));
                    } else {
                        pendingInvocations.add(paintablePendingRpc
                                .get(paintableIndex++));
                    }
                }
            }
        }
        return pendingInvocations;
    }
}
