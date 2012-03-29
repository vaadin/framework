/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.MouseEventDetailsBuilder;

public abstract class ClickEventHandler extends AbstractClickEventHandler {

    public static final String CLICK_EVENT_IDENTIFIER = "click";

    public ClickEventHandler(ComponentConnector connector) {
        this(connector, CLICK_EVENT_IDENTIFIER);
    }

    public ClickEventHandler(ComponentConnector connector,
            String clickEventIdentifier) {
        super(connector, clickEventIdentifier);
    }

    /**
     * Sends the click event based on the given native event. Delegates actual
     * sending to {@link #fireClick(MouseEventDetails)}.
     * 
     * @param event
     *            The native event that caused this click event
     */
    protected void fireClick(NativeEvent event) {
        MouseEventDetails mouseDetails = MouseEventDetailsBuilder
                .buildMouseEventDetails(event, getRelativeToElement());
        fireClick(event, mouseDetails);
    }

    /**
     * Sends the click event to the server. Must be implemented by sub classes,
     * typically by calling an RPC method.
     * 
     * @param event
     *            The event that caused this click to be fired
     * 
     * @param mouseDetails
     *            The mouse details for the event
     */
    protected abstract void fireClick(NativeEvent event,
            MouseEventDetails mouseDetails);

}