package com.vaadin.tests.components.ui;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.server.MissingFromDefaultWidgetsetComponent;

public class TestUIWidgetset2 extends AbstractTestUI {

    @Override
    protected void setup(WrappedRequest request) {
        addComponent(new MissingFromDefaultWidgetsetComponent());
    }

    @Override
    public String getTestDescription() {
        return "This contents if this UI should not work as the component is not present in DefaultWidgetSet";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7885);
    }

}
