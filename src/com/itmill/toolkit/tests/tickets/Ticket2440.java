package com.itmill.toolkit.tests.tickets;

import java.net.URL;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.DownloadStream;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.Window;

public class Ticket2440 extends Application {

    public void init() {
        final Window main = new MainWindow();
        setMainWindow(main);
        main
                .addComponent(new Label(
                        "Clicking the link should open a new window that should receive the URI 'msg/hello' and add that a a Label to it's ui. Currently the Label ends up in this (main) window (try reloading). Console intentionally spams during the window finding/uri handling - looks, uhm, interesting."));
    }

    public Window getWindow(String name) {
        System.err.println("Looking for " + name);
        if ("msg".equals(name)) {
            System.err.println(" rest uri, returning null");
            return null;
        }
        // If we already have the requested window, use it
        Window w = super.getWindow(name);
        if (w == null) {
            // If no window found, create it
            System.err.println(" new win");
            w = new MainWindow();
            w.setName(name);
            addWindow(w);
            return w;
        } else {
            System.err.println(" found win");
            return w;
        }
    }

    private class MainWindow extends Window {
        public MainWindow() {
            super("Main window");

            addComponent(new Link("new mainwin", new ExternalResource(
                    Ticket2440.this.getURL() + "msg/hello"), "_blank", -1, -1,
                    Window.BORDER_DEFAULT));
        }

        public DownloadStream handleURI(URL context, String relativeUri) {
            System.err.println((getMainWindow() == getWindow() ? "mainwin: "
                    : "subwin: ")
                    + context + ", " + relativeUri);
            if (relativeUri != null && relativeUri.startsWith("msg/")) {
                addComponent(new Label(relativeUri));
                return null;
            } else {
                return super.handleURI(context, relativeUri);
            }
        }

    }

}
