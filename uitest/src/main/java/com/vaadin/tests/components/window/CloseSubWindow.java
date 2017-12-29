package com.vaadin.tests.components.window;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class CloseSubWindow extends AbstractTestUIWithLog {

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

        Button removeButton = new Button("Remove from UI");
        removeButton.addClickListener(event -> getUI().removeWindow(window));
        layout.addComponent(removeButton);

        window.addCloseListener(event -> log("Window '" + title + "' closed"));

        return window;
    }

    @Override
    protected String getTestDescription() {
        return "Close sub-windows both from code and with the close button in the window title bar, and check for close events. Contains an ugly workaround for the Opera bug (Opera does not send close events)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3865;
    }
}
