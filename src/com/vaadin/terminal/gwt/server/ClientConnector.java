package com.vaadin.terminal.gwt.server;

import java.util.List;

import com.vaadin.terminal.gwt.client.Connector;

public interface ClientConnector extends Connector {
    /**
     * Returns the list of pending server to client RPC calls and clears the
     * list.
     * 
     * @return unmodifiable ordered list of pending server to client method
     *         calls (not null)
     * 
     * @since 7.0
     */
    public List<ClientMethodInvocation> retrievePendingRpcCalls();

}
