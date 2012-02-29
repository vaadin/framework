/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.communication;

import java.io.Serializable;

import com.vaadin.terminal.gwt.client.ApplicationConnection;

/**
 * Interface to be extended by all client to server RPC interfaces.
 * 
 * The nested interface InitializableClientToServerRpc has an
 * {@link #initRpc(String, ApplicationConnection)} method, which is created
 * automatically by a GWT generator and must be called on the client side before
 * generated implementations of the interface are used to perform RPC calls.
 * 
 * @since 7.0
 */
public interface ServerRpc extends Serializable {
    /**
     * Initialization support for client to server RPC interfaces.
     * 
     * This is in a separate interface instead of {@link ServerRpc} because
     * otherwise also server side proxies would have to implement the
     * initialization method.
     * 
     * @since 7.0
     */
    public interface InitializableClientToServerRpc extends ServerRpc {
        public void initRpc(String connectorId, ApplicationConnection client);
    }
}
