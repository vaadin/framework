package com.vaadin.tests.components.window;

import java.net.URL;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class CloseSubWindow extends TestBase {

    private Window browserWindow;

    @Override
    protected void setup() {
        Button openWindowButton = new Button("Open sub-window");
        openWindowButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                Window sub = createClosableSubWindow("Sub-window");
                getMainWindow().addWindow(sub);
            }
        });

        addComponent(openWindowButton);

        Button openBrowserWindowButton = new Button("Open browser window");
        openBrowserWindowButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                browserWindow = new Window("Window");
                browserWindow.addComponent(new Label("Close this window"));

                browserWindow.addListener(new CloseListener() {
                    public void windowClose(CloseEvent e) {
                        getMainWindow().showNotification(
                                "Browser window closed");
                        // there is no push, so the user needs to click a button
                        // to see the notification

                        // Opera does not send a notification about the window
                        // having been closed
                    }
                });

                addWindow(browserWindow);
                URL windowUrl = browserWindow.getURL();
                // named for easier access by test tools
                getMainWindow().open(new ExternalResource(windowUrl),
                        "nativewindow");
            }
        });

        addComponent(openBrowserWindowButton);

        addComponent(new Button("Poll server"));
    }

    private Window createClosableSubWindow(final String title) {
        final Window window = new Window(title);
        window.setSizeUndefined();
        window.getContent().setSizeFull();
        window.setClosable(true);

        Button closeButton = new Button("Close");
        closeButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                window.getParent().removeWindow(window);
            }
        });
        window.addComponent(closeButton);

        Button removeButton = new Button("Remove from parent");
        removeButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                window.getParent().removeWindow(window);
            }
        });
        window.addComponent(closeButton);

        window.addListener(new CloseListener() {
            public void windowClose(CloseEvent e) {
                getMainWindow().showNotification(title + " closed");
            }
        });

        return window;
    }

    @Override
    protected String getDescription() {
        return "Close sub-windows both from code and with the close button in the window title bar, and check for close events.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3865;
    }

}
