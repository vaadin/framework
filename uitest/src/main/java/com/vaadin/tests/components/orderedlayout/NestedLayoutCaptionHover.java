package com.vaadin.tests.components.orderedlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.VerticalLayout;

/**
 * Test hovering over nested layout caption
 *
 * @author Vaadin Ltd
 */
public class NestedLayoutCaptionHover extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout test = new VerticalLayout();
        test.setCaption("inner layout");
        addComponent(new VerticalLayout(
                new VerticalLayout(new VerticalLayout(test))));
    }

    @Override
    protected String getTestDescription() {
        return "Hovering over nested layout caption should not freeze the browser";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12469;
    }
}
