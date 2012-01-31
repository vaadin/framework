/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VTextAreaPaintable extends VTextFieldPaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Call parent renderer explicitly
        super.updateFromUIDL(uidl, client);

        if (uidl.hasAttribute("rows")) {
            getWidgetForPaintable().setRows(uidl.getIntAttribute("rows"));
        }

        if (getWidgetForPaintable().getMaxLength() >= 0) {
            getWidgetForPaintable().sinkEvents(Event.ONKEYUP);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTextArea.class);
    }

    @Override
    public VTextArea getWidgetForPaintable() {
        return (VTextArea) super.getWidgetForPaintable();
    }
}
