package com.vaadin.tests.widgetset.client;

import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ReplaceComponent;

@Connect(ReplaceComponent.class)
public class ReplacedConnector extends AbstractComponentConnector {

    @Override
    protected void init() {
        getWidget().setHTML(
                ReplacedConnector.class.getName() + ", should not be used");
    }

    @Override
    public HTML getWidget() {
        return (HTML) super.getWidget();
    }
}
