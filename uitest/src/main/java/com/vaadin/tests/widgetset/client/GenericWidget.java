package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.Label;

public class GenericWidget<T> extends Label {
    public void setGenericText(T value) {
        setText(String.valueOf(value));
    }
}
