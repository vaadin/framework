/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
import com.vaadin.shared.EventId;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.LayoutClickRpc;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.MouseEventDetailsBuilder;

public abstract class LayoutClickEventHandler extends AbstractClickEventHandler {

    public LayoutClickEventHandler(ComponentConnector connector) {
        this(connector, EventId.LAYOUT_CLICK_EVENT_IDENTIFIER);
    }

    public LayoutClickEventHandler(ComponentConnector connector,
            String clickEventIdentifier) {
        super(connector, clickEventIdentifier);
    }

    protected abstract ComponentConnector getChildComponent(Element element);

    protected ComponentConnector getChildComponent(NativeEvent event) {
        return getChildComponent((Element) event.getEventTarget().cast());
    }

    @Override
    protected void fireClick(NativeEvent event) {
        MouseEventDetails mouseDetails = MouseEventDetailsBuilder
                .buildMouseEventDetails(event, getRelativeToElement());
        getLayoutClickRPC().layoutClick(mouseDetails, getChildComponent(event));
    }

    protected abstract LayoutClickRpc getLayoutClickRPC();
}
