package com.vaadin.tests.layoutmanager;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.server.LayoutDuringStateUpdateComponent;

@Widgetset(TestingWidgetSet.NAME)
public class LayoutDuringStateUpdate extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new LayoutDuringStateUpdateComponent());
    }

}
