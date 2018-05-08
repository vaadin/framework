package com.vaadin.tests.debug;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Label;

@Widgetset(TestingWidgetSet.NAME)
public class PreserveCustomDebugSectionOpen extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label(
                "UI for testing that a custom debug window section remains open after refreshging."));
    }

}
