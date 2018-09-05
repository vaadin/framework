package com.vaadin.tests.widgetset.client.minitutorials.v7a2;

import com.google.gwt.user.client.ui.Label;

public class MyComponentWidget extends Label {
    public static final String CLASSNAME = "mycomponent";

    public MyComponentWidget() {
        setText("This is MyComponent");
        setStyleName(CLASSNAME);
    }
}
