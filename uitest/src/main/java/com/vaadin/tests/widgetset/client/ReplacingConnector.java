package com.vaadin.tests.widgetset.client;

import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.widgetset.server.ReplaceComponent;

@Connect(value = ReplaceComponent.class)
public class ReplacingConnector extends IntermediateReplaceConnector {
    @Override
    protected void init() {
        super.init();
        getWidget().setHTML(
                ReplacingConnector.class.getName() + ", this is the right one");
    }
}
