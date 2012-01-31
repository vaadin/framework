package com.vaadin.tests.dd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.VAbstractPaintableWidget;

public class VMyDropTargetPaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (client.updateComponent(this, uidl, true)) {
            return;
        }
        getWidgetForPaintable().client = client;
    }

    @Override
    public VMyDropTarget getWidgetForPaintable() {
        return (VMyDropTarget) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VMyDropTarget.class);
    }

}
