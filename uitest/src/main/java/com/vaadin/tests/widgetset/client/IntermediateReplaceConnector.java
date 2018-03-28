package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ReplaceComponent;

@Connect(value = ReplaceComponent.class)
public class IntermediateReplaceConnector extends ReplacedConnector {
    @Override
    protected void init() {
        super.init();
        getWidget().setHTML(IntermediateReplaceConnector.class.getName()
                + ", should not be used");
    }
}
