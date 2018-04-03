package com.vaadin.tests.application;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.DeferredComponent;

@Widgetset(TestingWidgetSet.NAME)
public class InitiallyDeferredLoading extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        DeferredComponent deferredComponent = new DeferredComponent();
        deferredComponent.setId("deferred");

        addComponent(deferredComponent);
    }

    @Override
    public String getTestDescription() {
        return "This UI contains a component from the deferred bundle. "
                + "It should still be visible as soon as the UI is loaded.";
    }

}
