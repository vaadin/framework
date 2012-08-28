package com.vaadin.shared.ui;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.communication.URLReference;

public class AbstractEmbeddedState extends ComponentState {

    protected URLReference source;
    protected String alternateText;

    public URLReference getSource() {
        return source;
    }

    public void setSource(URLReference source) {
        this.source = source;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

}
