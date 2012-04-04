/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.tests.dd;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.AbstractComponentConnector;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.tests.dd.CustomDDImplementation.MyDragSource;

@Component(MyDragSource.class)
public class MyDragSourceConnector extends AbstractComponentConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VMyDragSource.class);
    }

}
