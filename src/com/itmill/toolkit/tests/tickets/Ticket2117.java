package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class Ticket2117 extends Application {

    public void init() {
        setMainWindow(createWindow());
    }

    public Window getWindow(String name) {

        // If we already have the requested window, use it
        Window w = super.getWindow(name);
        if (w == null) {

            // If no window found, create it
            w = createExtraWindow(name);
            w.open(new ExternalResource(w.getURL()));
        }
        return w;
    }

    private Window createExtraWindow(String name) {
        final Window w = new Window("Extra window: " + name);
        w.setName(name);
        addWindow(w);
        w.addComponent(new Label(
                "This window has been created on fly for name: " + name));
        w.addComponent(new Label("It has also been redirected to " + w.getURL()
                + " to support reloading"));
        return w;
    }

    private Window createWindow() {
        final Window w = new Window();
        w.addComponent(new Label("Open this link: <a href='"
                + getURL().toExternalForm() + "'>" + getURL().toExternalForm()
                + "</a> in another browser-window.", Label.CONTENT_XHTML));
        return w;
    }
}
