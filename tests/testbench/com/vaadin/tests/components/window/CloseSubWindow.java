package com.vaadin.tests.components.window;

import java.net.URL;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class CloseSubWindow extends TestBase {

    private Window browserWindow;
    private Log log = new Log(5);

    @Override
    protected void setup() {
        Button openWindowButton = new Button("Open sub-window");
        openWindowButton.setDebugId("opensub");
        openWindowButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                Window sub = createClosableSubWindow("Sub-window");
                getMainWindow().addWindow(sub);
            }
        });

        addComponent(log);
        addComponent(openWindowButton);

        Button openBrowserWindowButton = new Button("Open browser window");
        openWindowButton.setDebugId("opennative");
        openBrowserWindowButton.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                browserWindow = new Window("Window");
                Button closeButton = new Button("Close this window",
                        new ClickListener() {

                            public void buttonClick(ClickEvent event) {
                                event.getButton().getWindow()
                                        .executeJavaScript("window.close();");

                            }
                        });
                browserWindow.addComponent(closeButton);

                browserWindow.addListener(new CloseListener() {
                    public void windowClose(CloseEvent e) {
                        logBrowserWindowClosed();
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
                if (getBrowser().isOpera()) {
                    // Immediately log a close event in Opera so this test can
                    // be run for all browsers. Vaadin ticket #5687.
                    logBrowserWindowClosed();
                }
            }
        });

        addComponent(openBrowserWindowButton);

        Button pollButton = new Button("Poll server");
        pollButton.setDebugId("poll");
        addComponent(pollButton);

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

    private void logBrowserWindowClosed() {
        log.log("Browser window closed");

    }

}
