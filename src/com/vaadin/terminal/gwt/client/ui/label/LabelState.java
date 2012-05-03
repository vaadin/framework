package com.vaadin.terminal.gwt.client.ui.label;

import com.vaadin.terminal.gwt.client.ComponentState;

public class LabelState extends ComponentState {
    private ContentMode contentMode = ContentMode.TEXT;
    private String text = "";

    public ContentMode getContentMode() {
        return contentMode;
    }

    public void setContentMode(ContentMode contentMode) {
        this.contentMode = contentMode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
