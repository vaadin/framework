package com.vaadin.tests.widgetset.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;

@Widgetset(TestingWidgetSet.NAME)
public class NoneLoadStyle extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return NoneLoadStyleComponent.class.getName()
                + " should resolve to UnknownComponentConnector";
    }

    @Override
    protected void setup(VaadinRequest request) {
        NoneLoadStyleComponent component = new NoneLoadStyleComponent();
        component.setId("component");
        addComponent(component);
    }
}
