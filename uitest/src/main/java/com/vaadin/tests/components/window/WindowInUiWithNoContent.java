package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Window;

/**
 * Test UI for Window attached to the UI with not content.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class WindowInUiWithNoContent extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // This is requires for the test
        setContent(null);

        Window window = new Window("window");
        addWindow(window);
    }

    @Override
    protected String getTestDescription() {
        return "Client UI component should not use VWindow as a content component";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13127;
    }

}
