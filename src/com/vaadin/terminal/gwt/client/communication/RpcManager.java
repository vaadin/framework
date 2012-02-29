/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.io.Serializable;

import com.vaadin.terminal.gwt.client.ConnectorMap;

/**
 * Client side RPC manager that can invoke methods based on RPC calls received
 * from the server.
 * 
 * A GWT generator is used to create an implementation of this class at
 * run-time.
 * 
 * @since 7.0
 */
public interface RpcManager extends Serializable {
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
            ConnectorMap connectorMap);
}
