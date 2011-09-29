package com.vaadin.tests.components.panel;

import com.vaadin.tests.components.AbstractComponentContainerTest;
import com.vaadin.ui.Panel;

public class PanelTest<T extends Panel> extends
        AbstractComponentContainerTest<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) Panel.class;
    }

}
