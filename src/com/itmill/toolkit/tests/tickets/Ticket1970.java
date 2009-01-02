package com.itmill.toolkit.tests.tickets;

import java.util.Iterator;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class Ticket1970 extends Application {

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
        }
        return w;
    }

    private Window createExtraWindow(String name) {
        final Window w = new Window("Extra window: " + name);
        w.setName(name);
        addWindow(w);
        w.addComponent(new Label(
                "This window has been created on fly for name: " + name));
        w.addComponent(new Button("Show open windows",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        String openWindows = "";
                        for (Iterator i = getWindows().iterator(); i.hasNext();) {
                            Window t = (Window) i.next();
                            openWindows += (openWindows.length() > 0 ? "," : "")
                                    + t.getName();
                        }
                        w.showNotification(openWindows);
                    }
                }));
        w.addListener(new Window.CloseListener() {
            public void windowClose(CloseEvent e) {
                removeWindow(w);
            }
        });

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
