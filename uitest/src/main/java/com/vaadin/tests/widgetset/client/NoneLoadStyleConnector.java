package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.Label;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.Connect.LoadStyle;
import com.vaadin.tests.widgetset.server.NoneLoadStyleComponent;

@Connect(value = NoneLoadStyleComponent.class, loadStyle = LoadStyle.NONE)
public class NoneLoadStyleConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        super.init();

        getWidget().setText(NoneLoadStyleConnector.class.getSimpleName());
    }

    @Override
    public Label getWidget() {
        return (Label) super.getWidget();
    }
}
