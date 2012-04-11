/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import com.vaadin.terminal.gwt.client.ServerConnector;

/**
 * Initialization support for client to server RPC interfaces.
 * 
 * This is in a separate interface used by the GWT generator class. The init
 * method is not in {@link ServerRpc} because then also server side proxies
 * would have to implement the initialization method.
 * 
 * @since 7.0
 */
public interface InitializableServerRpc extends ServerRpc {
    /**
     * Associates the RPC proxy with a connector. Called by generated code.
     * Should never be called manually.
     * 
     * @param connector
     *            The connector the ServerRPC instance is assigned to.
     */
    public void initRpc(ServerConnector connector);
}