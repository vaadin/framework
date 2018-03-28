package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

public class RedButtonUI extends UI {
    @Override
    protected void init(VaadinRequest request) {
        setContent(new RedButton("My red button"));
    }
}
