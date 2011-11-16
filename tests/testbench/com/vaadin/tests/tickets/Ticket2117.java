package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

public class Ticket2117 extends Application.LegacyApplication {

    @Override
    public void init() {
        setMainWindow(createWindow());
    }

    @Override
    public Root getWindow(String name) {

        // If we already have the requested window, use it
        Root w = super.getWindow(name);
        if (w == null) {

            // If no window found, create it
            w = createExtraWindow(name);
            w.open(new ExternalResource(getWindowUrl(w)));
        }
        return w;
    }

    private Root createExtraWindow(String name) {
        final Root w = new Root("Extra window: " + name);
        addWindow(w, name);
        w.addComponent(new Label(
                "This window has been created on fly for name: " + name));
        w.addComponent(new Label("It has also been redirected to "
                + getWindowUrl(w) + " to support reloading"));
        w.addComponent(new Button("button", new ClickListener() {
            public void buttonClick(ClickEvent event) {
                w.showNotification("Button clicked");
                w.addComponent(new Label("clicked"));
            }
        }));
        return w;
    }

    private Root createWindow() {
        final Root w = new Root();
        w.addComponent(new Label(
                "Click this link: <a target=\"_blank\" href='"
                        + getURL().toExternalForm()
                        + "'>"
                        + getURL().toExternalForm()
                        + "</a> which opens new windows to this uri. They should end up having a separate Window and URL.",
                Label.CONTENT_XHTML));
        return w;
    }
}
