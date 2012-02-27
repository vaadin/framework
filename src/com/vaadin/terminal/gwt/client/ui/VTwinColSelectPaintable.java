/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VTwinColSelectPaintable extends VOptionGroupBasePaintable {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Captions are updated before super call to ensure the widths are set
        // correctly
        if (isRealUpdate(uidl)) {
            getWidget().updateCaptions(uidl);
        }

        super.updateFromUIDL(uidl, client);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTwinColSelect.class);
    }

    @Override
    public VTwinColSelect getWidget() {
        return (VTwinColSelect) super.getWidget();
    }
}
