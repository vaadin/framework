package com.vaadin.tests.widgetset.server.gwtrpc;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.widgetset.TestingWidgetSet;

/**
 * Test the GWT RPC with Vaadin DevMode. See #11709.
 *
 * @author Vaadin Ltd
 */
@SuppressWarnings("serial")
@Widgetset(TestingWidgetSet.NAME)
public class GwtRpc extends AbstractReindeerTestUI {

    /**
     * Id of the button triggering the test case.
     */
    public static final String BUTTON_ID = "gwtRpcButton";

    @Override
    protected void setup(VaadinRequest request) {
        GwtRpcButton button = new GwtRpcButton();
        button.setId(BUTTON_ID);
        button.setCaption("Press me");

        addComponent(button);
    }

    @Override
    protected String getTestDescription() {
        return "Cannot call RPC in development mode";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11709;
    }

}
