/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.server;

import java.util.List;

import com.vaadin.terminal.gwt.client.Connector;

/**
 * Interface implemented by all connectors that are capable of communicating
 * with the client side
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 7.0.0
 * 
 */
public interface ClientConnector extends Connector {
    /**
     * Returns the list of pending server to client RPC calls and clears the
     * list.
     * 
     * @return an unmodifiable ordered list of pending server to client method
     *         calls (not null)
     * 
     * @since 7.0
     */
    public List<ClientMethodInvocation> retrievePendingRpcCalls();

}
