/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.server;

import java.io.Serializable;

import com.vaadin.terminal.VariableOwner;

/**
 * Marker interface for server side classes that can receive RPC calls.
 * 
 * This plays a role similar to that of {@link VariableOwner}.
 * 
 * @since 7.0
 */
public interface RpcTarget extends Serializable {
    /**
     * Returns the RPC manager instance to use when receiving calls for an RPC
     * interface.
     * 
     * @param rpcInterface
     *            interface for which the call was made
     * @return RpcManager or null if none found for the interface
     */
    public RpcManager getRpcManager(Class<?> rpcInterface);
}
