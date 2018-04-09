package com.vaadin.tests.components.ui;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.tests.widgetset.client.CustomUIConnectorRpc;

@Widgetset(TestingWidgetSet.NAME)
public class CustomUITest extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getRpcProxy(CustomUIConnectorRpc.class).test();
    }

    @Override
    protected String getTestDescription() {
        return "It should be possible to change the implementation of the UIConnector class";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10867);
    }

}
