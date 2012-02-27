/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VFormLayoutPaintable extends VAbstractPaintableWidgetContainer {
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;

        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().table.updateFromUIDL(uidl, client);
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        getWidget().table.updateCaption(component, uidl);
    }

    @Override
    public VFormLayout getWidget() {
        return (VFormLayout) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VFormLayout.class);
    }

}
