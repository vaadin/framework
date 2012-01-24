package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.gwt.client.communication.MethodInvocation;

/**
 * Server side RPC manager that can invoke methods based on RPC calls received
 * from the client.
 * 
 * @since 7.0
 */
public interface RpcManager {
    public void applyInvocation(MethodInvocation invocation);
}
