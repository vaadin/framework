/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.customfield;

import com.google.gwt.core.client.GWT;
import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.communication.SharedState;
import com.vaadin.shared.ui.Connect;
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
