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

package com.vaadin.client.communication;

import java.util.Collection;
import java.util.logging.Logger;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.ConnectorMap;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.metadata.Method;
import com.vaadin.client.metadata.NoDataException;
import com.vaadin.client.metadata.Type;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.MethodInvocation;

import elemental.json.JsonArray;

/**
 * Client side RPC manager that can invoke methods based on RPC calls received
 * from the server.
 * 
 * A GWT generator is used to create an implementation of this class at
 * run-time.
 * 
 * @since 7.0
 */
public class RpcManager {

    /**
     * Perform server to client RPC invocation.
     * 
     * @param invocation
     *            method to invoke
     */
    public void applyInvocation(MethodInvocation invocation,
            ServerConnector connector) {
        Method method = getMethod(invocation);

        Collection<ClientRpc> implementations = connector
                .getRpcImplementations(invocation.getInterfaceName());
        try {
            for (ClientRpc clientRpc : implementations) {
                method.invoke(clientRpc, invocation.getParameters());
            }
        } catch (NoDataException e) {
            throw new IllegalStateException("There is no information about "
                    + method.getSignature()
                    + ". Did you remember to compile the right widgetset?", e);
        }
    }

    /**
     * Gets the method that an invocation targets.
     * 
     * @param invocation
     *            the method invocation to get the method for
     * 
     * @since 7.4
     * @return the method targeted by this invocation
     */
    public static Method getMethod(MethodInvocation invocation) {
        // Implemented here instead of in MethodInovcation since it's in shared
        // and can't use our Method class.
        Type type = new Type(invocation.getInterfaceName(), null);
        Method method = type.getMethod(invocation.getMethodName());
        return method;
    }

    private static String getSignature(MethodInvocation invocation) {
        return invocation.getInterfaceName() + "." + invocation.getMethodName();
    }

    public Type[] getParameterTypes(MethodInvocation invocation) {
        Method method = getMethod(invocation);
        try {
            Type[] parameterTypes = method.getParameterTypes();
            return parameterTypes;
        } catch (NoDataException e) {
            throw new IllegalStateException("There is no information about "
                    + method.getSignature()
                    + ". Did you remember to compile the right widgetset?", e);
        }
    }

    public MethodInvocation parseAndApplyInvocation(JsonArray rpcCall,
            ApplicationConnection connection) {
        ConnectorMap connectorMap = ConnectorMap.get(connection);

        String connectorId = rpcCall.getString(0);
        String interfaceName = rpcCall.getString(1);
        String methodName = rpcCall.getString(2);
        JsonArray parametersJson = rpcCall.getArray(3);

        ServerConnector connector = connectorMap.getConnector(connectorId);

        MethodInvocation invocation = new MethodInvocation(connectorId,
                interfaceName, methodName);
        if (connector instanceof HasJavaScriptConnectorHelper) {
            ((HasJavaScriptConnectorHelper) connector)
                    .getJavascriptConnectorHelper().invokeJsRpc(invocation,
                            parametersJson);
        } else {
            if (connector == null) {
                throw new IllegalStateException("Target connector ("
                        + connector + ") not found for RCC to "
                        + getSignature(invocation));
            }

            parseMethodParameters(invocation, parametersJson, connection);
            getLogger().info("Server to client RPC call: " + invocation);
            applyInvocation(invocation, connector);
        }

        return invocation;
    }

    private void parseMethodParameters(MethodInvocation methodInvocation,
            JsonArray parametersJson, ApplicationConnection connection) {
        Type[] parameterTypes = getParameterTypes(methodInvocation);

        Object[] parameters = new Object[parametersJson.length()];
        for (int j = 0; j < parametersJson.length(); ++j) {
            parameters[j] = JsonDecoder.decodeValue(parameterTypes[j],
                    parametersJson.get(j), null, connection);
        }

        methodInvocation.setParameters(parameters);
    }

    private static Logger getLogger() {
        return Logger.getLogger(RpcManager.class.getName());
    }
}
