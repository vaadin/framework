package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.tests.widgetset.server.DeferredComponent;

@Connect(value = DeferredComponent.class, loadStyle = LoadStyle.DEFERRED)
public class DeferredConnector extends AbstractComponentConnector {
    @Override
    protected void init() {
        super.init();

        getWidget().setText("DeferredConnector");
    }

    @Override
    public Label getWidget() {
        return (Label) super.getWidget();
    }
}
