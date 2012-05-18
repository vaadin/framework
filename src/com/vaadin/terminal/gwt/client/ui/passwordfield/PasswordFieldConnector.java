/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.passwordfield;

import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.textfield.TextFieldConnector;
import com.vaadin.ui.PasswordField;

@Connect(PasswordField.class)
public class PasswordFieldConnector extends TextFieldConnector {

    @Override
    public VPasswordField getWidget() {
        return (VPasswordField) super.getWidget();
    }
}
