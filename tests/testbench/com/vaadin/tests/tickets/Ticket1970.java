package com.vaadin.tests.tickets;

import java.util.Iterator;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

public class Ticket1970 extends Application.LegacyApplication {

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
        }
        return w;
    }

    private Root createExtraWindow(String name) {
        final Root w = new Root("Extra window: " + name);
        addWindow(w, name);
        w.addComponent(new Label(
                "This window has been created on fly for name: " + name));
        w.addComponent(new Button("Show open windows",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        String openWindows = "";
                        for (Iterator<Root> i = getWindows().iterator(); i
                                .hasNext();) {
                            Root t = i.next();
                            openWindows += (openWindows.length() > 0 ? "," : "")
                                    + getWindowName(t);
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

    private Root createWindow() {
        final Root w = new Root();
        w.addComponent(new Button("Show the name of the application",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        w.showNotification("Name of this window = "
                                + getWindowName(w));
                    }
                }));
        w.addComponent(new Label("<a href='" + getURL().toExternalForm() + "'>"
                + getURL().toExternalForm() + "</a>", Label.CONTENT_XHTML));
        w.addComponent(new Label(
                "<h2>How to reproduce</h2>Open the above link in another browser"
                        + " window and then press the Show-button on this window.",
                Label.CONTENT_XHTML));

        return w;
    }
}
