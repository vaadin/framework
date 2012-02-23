/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

import com.vaadin.terminal.gwt.client.communication.MethodInvocation;

/**
 * Server side RPC manager that can invoke methods based on RPC calls received
 * from the client.
 * 
 * @since 7.0
 */
public interface RpcManager extends Serializable {
    public void applyInvocation(MethodInvocation invocation);
}
