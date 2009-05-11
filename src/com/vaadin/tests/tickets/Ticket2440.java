package com.vaadin.tests.tickets;

import java.net.URL;

import com.vaadin.Application;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.URIHandler;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Window;

public class Ticket2440 extends Application {

    @Override
    public void init() {
        final Window main = new MainWindow();
        setMainWindow(main);
        main
                .addComponent(new Label(
                        "Clicking the link should open a new window that should receive the URI 'msg/hello' and add that a a Label to it's ui. Currently the Label ends up in this (main) window (try reloading). Console intentionally spams during the window finding/uri handling - looks, uhm, interesting."));
    }

    @Override
    public Window getWindow(String name) {
        System.err.println("Looking for " + name);
        if ("msg".equals(name)) {
            System.err
                    .println(" rest uri, returning new MainWindow with message from uri");
            MainWindow restWindow = new MainWindow();
            addWindow(restWindow);
            return restWindow;
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

            addURIHandler(new URIHandler() {
                public DownloadStream handleURI(URL context, String relativeUri) {
                    System.err
                            .println((getMainWindow() == getWindow() ? "mainwin: "
                                    : "subwin: ")
                                    + context + ", " + relativeUri);
                    addComponent(new Label(relativeUri));
                    return null;
                }
            });
        }

        @Override
        public DownloadStream handleURI(URL context, String relativeUri) {
            System.err.println("MainWindow.handleURI();");
            return super.handleURI(context, relativeUri);
        }

    }

}
