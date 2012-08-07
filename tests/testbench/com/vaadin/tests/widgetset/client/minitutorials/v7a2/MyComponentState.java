package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.vaadin.terminal.gwt.client.ComponentState;

public class MyComponentState extends ComponentState {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}