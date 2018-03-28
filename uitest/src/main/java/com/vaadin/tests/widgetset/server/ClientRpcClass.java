package com.vaadin.tests.widgetset.server;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;

@Widgetset(TestingWidgetSet.NAME)
public class ClientRpcClass extends AbstractReindeerTestUI {

    public static String TEST_COMPONENT_ID = "testComponent";

    @Override
    protected void setup(VaadinRequest request) {
        ClientRpcClassComponent component = new ClientRpcClassComponent();
        component.setId(TEST_COMPONENT_ID);
        addComponent(component);

        component.pause();
    }

    @Override
    protected String getTestDescription() {
        return "UI showing dummy component where the wiget type is implementing the RPC interface.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13056);
    }

}
