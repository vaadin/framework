package com.vaadin.tests.components.root;

import com.vaadin.annotations.Widgetset;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;

@Widgetset("invalid")
public class TestRootWidgetset extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        // Nothing here
    }

    @Override
    public String getTestDescription() {
        return "This root should never load, as the widgetset can not be loaded";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
