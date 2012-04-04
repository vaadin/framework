/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.ui.PasswordField;

@Component(PasswordField.class)
public class PasswordFieldConnector extends TextFieldConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        super.updateFromUIDL(uidl, client);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VPasswordField.class);
    }

    @Override
    public VPasswordField getWidget() {
        return (VPasswordField) super.getWidget();
    }
}
