package com.vaadin.tests.tickets;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2117 extends LegacyApplication {

    @Override
    public void init() {
        setMainWindow(createWindow());
    }

    @Override
    public LegacyWindow getWindow(String name) {

        // If we already have the requested window, use it
        LegacyWindow w = super.getWindow(name);
        if (w == null) {

            // If no window found, create it
            w = createExtraWindow(name);
            w.open(new ExternalResource(w.getURL()));
        }
        return w;
    }

    private LegacyWindow createExtraWindow(String name) {
        final LegacyWindow w = new LegacyWindow("Extra window: " + name);
        w.setName(name);
        addWindow(w);
        w.addComponent(new Label(
                "This window has been created on fly for name: " + name));
        w.addComponent(new Label("It has also been redirected to " + w.getURL()
                + " to support reloading"));
        w.addComponent(new Button("button", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                w.showNotification("Button clicked");
                w.addComponent(new Label("clicked"));
            }
        }));
        return w;
    }

    private LegacyWindow createWindow() {
        final LegacyWindow w = new LegacyWindow();
        w.addComponent(new Label(
                "Click this link: <a target=\"_blank\" href='"
                        + getURL().toExternalForm()
                        + "'>"
                        + getURL().toExternalForm()
                        + "</a> which opens new windows to this uri. They should end up having a separate Window and URL.",
                ContentMode.HTML));
        return w;
    }
}
