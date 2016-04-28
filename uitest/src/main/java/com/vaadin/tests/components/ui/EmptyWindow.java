package com.vaadin.tests.components.ui;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class EmptyWindow extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addWindow(new Window("My empty window"));
        setContent(new Label("UI"));
    }

    @Override
    protected String getTestDescription() {
        return "There should be an empty window on the screen. Currently it should have the min width defined in VWindow";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10325;
    }

}
