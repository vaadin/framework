package com.vaadin.tests.widgetset.server;

import com.vaadin.tests.widgetset.client.UseStateFromHierachyChangeConnectorState;
import com.vaadin.ui.AbstractSingleComponentContainer;
import com.vaadin.ui.Component;

public class UseStateFromHierachyComponent
        extends AbstractSingleComponentContainer {

    @Override
    protected UseStateFromHierachyChangeConnectorState getState() {
        return (UseStateFromHierachyChangeConnectorState) super.getState();
    }

    @Override
    public void setContent(Component content) {
        getState().child = content;
        super.setContent(content);
    }

}
