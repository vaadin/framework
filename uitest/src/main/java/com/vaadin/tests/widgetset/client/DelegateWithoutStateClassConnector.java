package com.vaadin.tests.widgetset.client;

import com.google.gwt.core.shared.GWT;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.DelegateWithoutStateClassComponent;
import com.vaadin.v7.client.ui.textarea.TextAreaConnector;

@Connect(DelegateWithoutStateClassComponent.class)
public class DelegateWithoutStateClassConnector extends TextAreaConnector {
    @Override
    public VExtendedTextArea getWidget() {
        return (VExtendedTextArea) super.getWidget();
    }

    @Override
    protected VExtendedTextArea createWidget() {
        return GWT.create(VExtendedTextArea.class);
    }
}
