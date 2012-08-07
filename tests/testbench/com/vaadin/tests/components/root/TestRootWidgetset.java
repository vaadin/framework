package com.vaadin.tests.components.root;

import com.vaadin.annotations.Widgetset;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class TestRootWidgetset extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new MissingFromDefaultWidgetsetComponent());
    }

    @Override
    public String getTestDescription() {
        return "This contents if this root should work as the component is present in TestingWidgetSet";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
