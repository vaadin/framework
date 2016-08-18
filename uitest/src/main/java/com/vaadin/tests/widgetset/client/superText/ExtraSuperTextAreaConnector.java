package com.vaadin.tests.widgetset.client.superText;

import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ExtraSuperTextArea;
import com.vaadin.v7.client.ui.textarea.TextAreaConnector;

@Connect(ExtraSuperTextArea.class)
public class ExtraSuperTextAreaConnector extends TextAreaConnector {

    // @DelegateToWidget will not work with overridden state
    @Override
    public ExtraSuperTextAreaState getState() {
        return (ExtraSuperTextAreaState) super.getState();
    }
}