package com.vaadin.tests.components.accordion;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;

/**
 * Test for removing component from Accordion.
 *
 * @author Vaadin Ltd
 */
public class AccordionRemoveComponent extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Accordion accordion = new Accordion();
        Button button = new Button("remove");
        button.addClickListener(
                event -> accordion.removeComponent(event.getButton()));
        accordion.addComponent(button);
        addComponent(accordion);
    }

    @Override
    protected String getTestDescription() {
        return "Reset selected index when tab is removed";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17248;
    }
}
