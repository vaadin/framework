/*
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui.textarea;

import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.textarea.TextAreaState;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.textfield.TextFieldConnector;
import com.vaadin.ui.TextArea;

@Connect(TextArea.class)
public class TextAreaConnector extends TextFieldConnector {

    @Override
    public TextAreaState getState() {
        return (TextAreaState) super.getState();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getWidget().setRows(getState().getRows());
        getWidget().setWordwrap(getState().isWordwrap());
    }

    @Override
    public VTextArea getWidget() {
        return (VTextArea) super.getWidget();
    }
}
