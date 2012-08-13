/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import com.vaadin.shared.communication.ClientRpc;
import com.vaadin.shared.communication.MethodInvocation;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.ServerConnector;
import com.vaadin.terminal.gwt.client.VConsole;

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

    private final Map<String, RpcMethod> methodMap = new HashMap<String, RpcMethod>();

    public RpcManager() {
        GeneratedRpcMethodProvider provider = GWT
                .create(GeneratedRpcMethodProvider.class);
        Collection<RpcMethod> methods = provider.getGeneratedRpcMethods();
        for (RpcMethod rpcMethod : methods) {
            methodMap.put(
                    rpcMethod.getInterfaceName() + "."
                            + rpcMethod.getMethodName(), rpcMethod);
        }
    }

    /**
     * Perform server to client RPC invocation.
     * 
     * @param invocation
     *            method to invoke
     */
    public void applyInvocation(MethodInvocation invocation,
            ServerConnector connector) {
        String signature = getSignature(invocation);

        RpcMethod rpcMethod = getRpcMethod(signature);
        Collection<ClientRpc> implementations = connector
                .getRpcImplementations(invocation.getInterfaceName());
        for (ClientRpc clientRpc : implementations) {
            rpcMethod.applyInvocation(clientRpc, invocation.getParameters());
        }
    }

    private RpcMethod getRpcMethod(String signature) {
        RpcMethod rpcMethod = methodMap.get(signature);
        if (rpcMethod == null) {
            throw new IllegalStateException("There is no information about "
                    + signature
                    + ". Did you remember to compile the right widgetset?");
        }
        return rpcMethod;
    }

    private static String getSignature(MethodInvocation invocation) {
        return invocation.getInterfaceName() + "." + invocation.getMethodName();
    }

    public Type[] getParameterTypes(MethodInvocation invocation) {
        return getRpcMethod(getSignature(invocation)).getParameterTypes();
    }

    public void parseAndApplyInvocation(JSONArray rpcCall,
            ApplicationConnection connection) {
        ConnectorMap connectorMap = ConnectorMap.get(connection);

        String connectorId = ((JSONString) rpcCall.get(0)).stringValue();
        String interfaceName = ((JSONString) rpcCall.get(1)).stringValue();
        String methodName = ((JSONString) rpcCall.get(2)).stringValue();
        JSONArray parametersJson = (JSONArray) rpcCall.get(3);

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
            VConsole.log("Server to client RPC call: " + invocation);
            applyInvocation(invocation, connector);
        }
    }

    private void parseMethodParameters(MethodInvocation methodInvocation,
            JSONArray parametersJson, ApplicationConnection connection) {
        Type[] parameterTypes = getParameterTypes(methodInvocation);

        Object[] parameters = new Object[parametersJson.size()];
        for (int j = 0; j < parametersJson.size(); ++j) {
            parameters[j] = JsonDecoder.decodeValue(parameterTypes[j],
                    parametersJson.get(j), null, connection);
        }

        methodInvocation.setParameters(parameters);
    }

}
