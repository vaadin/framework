package com.vaadin.terminal.gwt.client.ui;

import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.communication.ServerRpc;

public interface ClickRPC extends ServerRpc {
    /**
     * Called when a click event has occurred and there are server side
     * listeners for the event.
     * 
     * @param mouseDetails
     *            Details about the mouse when the event took place
     */
    public void click(MouseEventDetails mouseDetails);
}