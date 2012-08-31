package com.vaadin.shared.ui;

import com.vaadin.shared.ComponentState;

public class AbstractEmbeddedState extends ComponentState {

    public static final String SOURCE_RESOURCE = "source";

    private String alternateText;

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

}
