package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.communication.URLReference;

public class ResourceInStateState extends ComponentState {

    private URLReference myIcon;

    public URLReference getMyIcon() {
        return myIcon;
    }

    public void setMyIcon(URLReference icon) {
        myIcon = icon;
    }
}