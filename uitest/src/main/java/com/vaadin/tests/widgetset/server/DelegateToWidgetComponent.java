package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.widgetset.client.DelegateState;
import com.vaadin.ui.AbstractComponent;

public class DelegateToWidgetComponent extends AbstractComponent {
    public DelegateToWidgetComponent() {
        DelegateState state = getState();
        state.value1 = "My String";
        state.renamedValue2 = 42;
        state.setValue3(Boolean.TRUE);
        state.setRenamedValue4(Math.PI);
    }

    @Override
    protected DelegateState getState() {
        return (DelegateState) super.getState();
    }
}
