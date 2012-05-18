/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.dd;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.tests.dd.CustomDDImplementation.MyDropTarget;

@Connect(MyDropTarget.class)
public class MyDropTargetConnector extends AbstractComponentConnector implements
        Paintable {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }
        getWidget().client = client;
    }

    @Override
    public VMyDropTarget getWidget() {
        return (VMyDropTarget) super.getWidget();
    }

}
