package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface LayoutClickRPC extends ServerRpc {
    /**
     * Called when a layout click event has occurred and there are server
     * side listeners for the event.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     * @param clickedConnector
     *            The child component that was the target of the event
     */
    public void layoutClick(MouseEventDetails mouseDetails,
            Connector clickedConnector);
}