package com.vaadin.tests.components.uitest;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class UIScrolling extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Set layout to high enough to get scroll.
        getLayout().setHeight("2250px");
        addComponent(new Button("scroll to 1000px",
                event -> UI.getCurrent().setScrollTop(1000)));
        addComponent(new Button(
                "This button is halfway down. Click to report scroll position.",
                event -> Notification.show("Scrolled to "
                        + event.getButton().getUI().getScrollTop() + " px")));
    }

    @Override
    protected String getTestDescription() {
        return "Windows can be programmatically scrolled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9952;
    }

}
