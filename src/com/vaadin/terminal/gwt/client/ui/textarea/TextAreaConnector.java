/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.textarea;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.textfield.TextFieldConnector;
import com.vaadin.ui.TextArea;

@Component(TextArea.class)
public class TextAreaConnector extends TextFieldConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Call parent renderer explicitly
        super.updateFromUIDL(uidl, client);

        if (uidl.hasAttribute("rows")) {
            getWidget().setRows(uidl.getIntAttribute("rows"));
        }

        if (getWidget().getMaxLength() >= 0) {
            getWidget().sinkEvents(Event.ONKEYUP);
        }
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VTextArea.class);
    }

    @Override
    public VTextArea getWidget() {
        return (VTextArea) super.getWidget();
    }
}
