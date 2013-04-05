package com.vaadin.tests.components.window;

import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class CloseSubWindow extends TestBase {

    private Log log = new Log(5);

    @Override
    protected void setup() {
        Button openWindowButton = new Button("Open sub-window");
        openWindowButton.setId("opensub");
        openWindowButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Window sub = createClosableSubWindow("Sub-window");
                getMainWindow().addWindow(sub);
            }
        });

        addComponent(log);
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
        closeButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                event.getButton().findAncestor(Window.class).close();
            }
        });
        layout.addComponent(closeButton);

        Button removeButton = new Button("Remove from parent");
        removeButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                window.close();
            }
        });
        layout.addComponent(closeButton);

        window.addListener(new CloseListener() {
            @Override
            public void windowClose(CloseEvent e) {
                log.log("Window '" + title + "' closed");
            }
        });

        return window;
    }

    @Override
    protected String getDescription() {
        return "Close sub-windows both from code and with the close button in the window title bar, and check for close events. Contains an ugly workaround for the Opera bug (Opera does not send close events)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3865;
    }
}
