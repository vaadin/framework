package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class WindowPreCloseListener extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Button openWindowButton = new Button("Open sub-window");
        openWindowButton.setId("opensub");
        openWindowButton.addClickListener(event -> {
            Window sub = createClosableSubWindow("Sub-window");
            getUI().addWindow(sub);
        });

        addComponent(openWindowButton);
    }

    private Window createClosableSubWindow(final String title) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSizeUndefined();
        final Window window = new Window(title, layout);
        window.setSizeUndefined();
        window.setClosable(true);

        Button closeButton = new Button("Close");
        closeButton.addClickListener(
                event -> event.getButton().findAncestor(Window.class).close());
        layout.addComponent(closeButton);

        window.addCloseListener(event -> {
            log("Window '" + title + "' closed");
        });

        window.addPreCloseListener(event -> {
            event.setClosePrevented(true);

            log("Window '" + title + "' close attempt prevented");
        });

        return window;
    }

    @Override
    protected String getTestDescription() {
        return "Try to close window both from code and from client side, and check for close events when PreCloseListener prevents closing.";
    }
}
