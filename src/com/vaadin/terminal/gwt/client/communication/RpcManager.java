/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.ServerConnector;

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
     * @param connectorMap
     *            mapper used to find Connector for the method call and any
     *            connectors referenced in parameters
     */
    public void applyInvocation(MethodInvocation invocation,
            ConnectorMap connectorMap) {
        ServerConnector connector = connectorMap.getConnector(invocation
                .getConnectorId());
        String signature = getSignature(invocation);
        if (connector == null) {
            throw new IllegalStateException("Target connector ("
                    + invocation.getConnectorId() + ") not found for RCC to "
                    + signature);
        }

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

}
