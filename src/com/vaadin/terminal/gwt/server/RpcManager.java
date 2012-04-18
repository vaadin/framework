/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

/**
 * Server side RPC manager that can invoke methods based on RPC calls received
 * from the client.
 * 
 * @since 7.0
 */
public interface RpcManager extends Serializable {
    public void applyInvocation(ServerRpcMethodInvocation invocation);
}
