/**
 *
 */
package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.TextField;

public class TooltipOnRequiredIndicator extends AbstractReindeerTestUI {

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();

        TextField inVertical = new TextField();
        inVertical.setRequired(true);
        inVertical.setRequiredError("Vertical layout tooltip");
        inVertical.setCaption("Vertical layout caption");
        inVertical.setId("verticalField");

        layout.addComponent(inVertical);
        addComponent(layout);

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        TextField inHorizontal = new TextField();
        inHorizontal.setRequired(true);
        inHorizontal.setRequiredError("Horizontal layout tooltip");
        inHorizontal.setCaption("Horizontal layout caption");
        inHorizontal.setId("horizontalField");

        horizontalLayout.addComponent(inHorizontal);
        layout.addComponent(horizontalLayout);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Show tooltip for caption and required indicator";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 10046;
    }

}
