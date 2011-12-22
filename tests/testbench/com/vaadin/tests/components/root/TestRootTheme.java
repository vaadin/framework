package com.vaadin.tests.components.root;

import com.vaadin.annotations.Theme;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.ui.Label;

@Theme("tests-tickets")
public class TestRootTheme extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        Label label = new Label("A red label");
        label.setStyleName("red");
        addComponent(label);
    }

    @Override
    public String getTestDescription() {
        return "Root with @RootTheme(\"tests-tickets\")";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
