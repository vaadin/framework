/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.vaadin.terminal.gwt.client.ServerConnector;

public abstract class AbstractServerConnectorEvent<H extends EventHandler>
        extends GwtEvent<H> {
    private ServerConnector connector;

    protected AbstractServerConnectorEvent() {
    }

    public ServerConnector getConnector() {
        return connector;
    }

    public void setConnector(ServerConnector connector) {
        this.connector = connector;
    }

    /**
     * Sends this event to the given handler.
     * 
     * @param handler
     *            The handler to dispatch.
     */
    @Override
    public abstract void dispatch(H handler);
}
