package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Ticket2117 extends Application {

    @Override
    public void init() {
        setMainWindow(createWindow());
    }

    @Override
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
        w.addComponent(new Button("button", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                w.showNotification("Button clicked");
                w.addComponent(new Label("clicked"));
            }
        }));
        return w;
    }

    private Window createWindow() {
        final Window w = new Window();
        w
                .addComponent(new Label(
                        "Click this link: <a target=\"_blank\" href='"
                                + getURL().toExternalForm()
                                + "'>"
                                + getURL().toExternalForm()
                                + "</a> which opens new windows to this uri. They should end up having a separate Window and URL.",
                        Label.CONTENT_XHTML));
        return w;
    }
}
