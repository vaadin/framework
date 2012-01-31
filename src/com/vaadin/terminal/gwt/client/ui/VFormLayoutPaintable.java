package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VFormLayoutPaintable extends VAbstractPaintableWidgetContainer {
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;

        getWidgetForPaintable().client = client;

        if (client.updateComponent(this, uidl, true)) {
            getWidgetForPaintable().rendering = false;
            return;
        }

        getWidgetForPaintable().table.updateFromUIDL(uidl, client);

        getWidgetForPaintable().rendering = false;
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        getWidgetForPaintable().table.updateCaption(component, uidl);
    }

    @Override
    public VFormLayout getWidgetForPaintable() {
        return (VFormLayout) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VFormLayout.class);
    }

}
