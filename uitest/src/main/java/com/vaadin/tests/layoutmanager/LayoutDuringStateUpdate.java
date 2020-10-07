package com.vaadin.tests.layoutmanager;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.LayoutDuringStateUpdateComponent;
import com.vaadin.ui.Button;

@Widgetset(TestingWidgetSet.NAME)
public class LayoutDuringStateUpdate extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // delay adding of the component to ensure unrelated layouting calls
        // don't interfere with the test
        addComponent(new Button("Add component",
                e -> addComponent(new LayoutDuringStateUpdateComponent())));
    }
}
