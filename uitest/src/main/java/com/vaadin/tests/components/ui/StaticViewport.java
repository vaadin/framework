package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Viewport;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

@Viewport("myViewport")
public class StaticViewport extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label("I should have a static viewport tag"));
    }

}
