package com.vaadin.tests.components.customlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

public class CustomLayoutWithoutTemplate extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        CustomLayout cl = new CustomLayout("missing-layout-file.html");
        cl.addComponent(new Label("This Label should be visible."), "foo");
        cl.addComponent(new Button("And this Button too."), "bar");

        addComponent(cl);
    }

    @Override
    protected String getTestDescription() {
        return "Verify that CustomLayout renders child components even if the template is missing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8696;
    }
}
