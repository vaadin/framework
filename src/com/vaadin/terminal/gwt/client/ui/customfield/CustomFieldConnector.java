/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.customfield;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.customcomponent.CustomComponentConnector;
import com.vaadin.terminal.gwt.client.ui.customcomponent.VCustomComponent;
import com.vaadin.ui.CustomField;

@Component(value = CustomField.class)
public class CustomFieldConnector extends CustomComponentConnector {

    @Override
    protected Widget createWidget() {
        return GWT.create(VCustomComponent.class);
    }

    @Override
    public VCustomComponent getWidget() {
        return super.getWidget();
    }
}
