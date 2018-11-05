package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 * @since 7.1.9
 * @author Vaadin Ltd
 */
public class ScrollingBodyElementWithModalOpened
        extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setHeight("10000px");

        Window window = new Window("Caption");

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("300px");
        layout.setHeight("300px");
        window.setContent(layout);

        addWindow(window);

        window.setModal(true);

        addComponent(verticalLayout);
    }

    @Override
    protected String getTestDescription() {
        return "Screen must not scroll with modal opened.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12899;
    }
}
