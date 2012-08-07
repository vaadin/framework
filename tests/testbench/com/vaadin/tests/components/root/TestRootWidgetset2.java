package com.vaadin.tests.components.root;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestRoot;
import com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent;

public class TestRootWidgetset2 extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new MissingFromDefaultWidgetsetComponent());
    }

    @Override
    public String getTestDescription() {
        return "This contents if this root should not work as the component is not present in DefaultWidgetSet";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
