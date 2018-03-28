package com.vaadin.tests.components.customlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public class CustomLayoutWithNullTemplate extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CustomLayout cl = new CustomLayout();
        cl.addComponent(new Label("This Label should be visible."), "foo");
        cl.addComponent(new Button("This Button too."), "bar");

        addComponent(cl);
    }

    @Override
    protected String getTestDescription() {
        return "Verify that a default-constructed CustomLayout renders child components";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17210;
    }
}
