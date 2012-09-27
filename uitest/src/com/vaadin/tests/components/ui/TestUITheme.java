package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

@Theme("tests-tickets")
public class TestUITheme extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Label label = new Label("A green label");
        label.setStyleName("green");
        addComponent(label);
    }

    @Override
    public String getTestDescription() {
        return "UI with @Theme(\"tests-tickets\"). The label uses a stylename that should only be defined in the test-tickets theme.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
