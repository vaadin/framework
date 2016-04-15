package com.vaadin.tests.components.panel;

import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.Panel;

public class PanelTest<T extends Panel> extends AbstractComponentTest<T> {

    @SuppressWarnings("unchecked")
    @Override
    protected Class<T> getTestClass() {
        return (Class<T>) Panel.class;
    }

}
