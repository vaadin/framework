package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class UncloseableWindowCloseShortcut extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Window uncloseable = new Window("Uncloseable",
                new Label("Try and close me with esc"));
        uncloseable.setClosable(false);
        addWindow(uncloseable);

        uncloseable.center();
        uncloseable.focus();
    }

    @Override
    protected String getTestDescription() {
        return "An uncloseable Window should not be closed with esc key.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19700;
    }

}
