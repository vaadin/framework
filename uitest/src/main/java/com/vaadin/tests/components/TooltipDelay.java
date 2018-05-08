package com.vaadin.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;

/**
 * Test to see if tooltip delay is working properly.
 *
 * @author Vaadin Ltd
 */
public class TooltipDelay extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest vaadinRequest) {

        Button button = new Button("Expand");
        button.setDescription("Expand");
        addComponent(button);

        getTooltipConfiguration().setOpenDelay(5000);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Tooltips should appear with a five second delay.";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 13695;
    }

}
