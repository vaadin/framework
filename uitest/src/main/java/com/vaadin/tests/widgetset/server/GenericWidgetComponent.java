package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.widgetset.client.GenericWidgetState;
import com.vaadin.ui.AbstractComponent;

public class GenericWidgetComponent extends AbstractComponent {

    @Override
    protected GenericWidgetState getState() {
        return (GenericWidgetState) super.getState();
    }

    public void setGenericText(String genericText) {
        getState().genericText = genericText;
    }
}
