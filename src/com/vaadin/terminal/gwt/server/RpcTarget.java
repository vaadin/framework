package com.vaadin.terminal.gwt.server;

import com.vaadin.terminal.VariableOwner;

/**
 * Marker interface for server side classes that can receive RPC calls.
 * 
 * This plays a role similar to that of {@link VariableOwner}.
 * 
 * @since 7.0
 */
public interface RpcTarget {
    public RpcManager getRpcManager();
}
