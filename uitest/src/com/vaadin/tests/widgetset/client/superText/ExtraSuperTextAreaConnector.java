package com.vaadin.tests.widgetset.client.superText;

import com.vaadin.client.ui.textarea.TextAreaConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ExtraSuperTextArea;

@Connect(ExtraSuperTextArea.class)
public class ExtraSuperTextAreaConnector extends TextAreaConnector {

    // @DelegateToWidget will not work with overridden state
    @Override
    public ExtraSuperTextAreaState getState() {
        return (ExtraSuperTextAreaState) super.getState();
    }
}