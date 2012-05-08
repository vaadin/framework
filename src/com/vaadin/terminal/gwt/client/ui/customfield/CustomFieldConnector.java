/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.customfield;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.AbstractFieldState;
import com.vaadin.terminal.gwt.client.communication.SharedState;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.customcomponent.CustomComponentConnector;
import com.vaadin.ui.CustomField;

@Connect(value = CustomField.class)
public class CustomFieldConnector extends CustomComponentConnector {
    @Override
    protected SharedState createState() {
        // Workaround as CustomFieldConnector does not extend
        // AbstractFieldConnector.
        return GWT.create(AbstractFieldState.class);
    }

}
