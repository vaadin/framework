package com.vaadin.tests.components.abstractcomponent;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;

public class RemSizeUnitTest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("My height is 10.5 x 5  rem");
        label.setHeight("5rem");
        label.setWidth(10.5f, Unit.REM);

        addComponent(label);
    }

    @Override
    protected String getTestDescription() {
        return "Tests that REM units are properly applied to the DOM";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(11279);
    }

}
