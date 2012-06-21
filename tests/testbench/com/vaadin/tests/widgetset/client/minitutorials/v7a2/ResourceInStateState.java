package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.communication.URLReference;

public class ResourceInStateState extends ComponentState {

    private URLReference myIcon;

    public URLReference getMyIcon() {
        return myIcon;
    }

    public void setMyIcon(URLReference icon) {
        myIcon = icon;
    }
}