package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1970 extends Application {

    public void init() {
        setMainWindow(createWindow());
    }

    public Window getWindow(String name) {
        Window w = super.getWindow(name);
        if (w == null) {
            w = new Window("Extra window: " + name);
            w.setName(name);
            addWindow(w);
        }
        return w;
    }

    private Window createWindow() {
        final Window w = new Window();
        w.addComponent(new Button("Show the name of the application",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        w.showNotification("Name of this window = "
                                + w.getName());
                    }
                }));
        w.addComponent(new Label("<a href='" + getURL().toExternalForm() + "'>"
                + getURL().toExternalForm() + "</a>", Label.CONTENT_XHTML));
        w
                .addComponent(new Label(
                        "<h2>How to reproduce</h2>Open the above link in another browser"
                                + " window and then press the Show-button on this window.",
                        Label.CONTENT_XHTML));

        return w;
    }
}
