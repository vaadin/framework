/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.dd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;

public class VMyDropTargetPaintable extends AbstractComponentConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
        getWidget().client = client;
    }

    @Override
    public VMyDropTarget getWidget() {
        return (VMyDropTarget) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VMyDropTarget.class);
    }

}
