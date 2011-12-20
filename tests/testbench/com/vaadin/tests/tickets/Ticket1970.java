package com.vaadin.tests.tickets;

import java.util.Iterator;

import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root.LegacyWindow;

public class Ticket1970 extends Application.LegacyApplication {

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
        }
        return w;
    }

    private LegacyWindow createExtraWindow(String name) {
        final LegacyWindow w = new LegacyWindow("Extra window: " + name);
        w.setName(name);
        addWindow(w);
        w.addComponent(new Label(
                "This window has been created on fly for name: " + name));
        w.addComponent(new Button("Show open windows",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        String openWindows = "";
                        for (Iterator<LegacyWindow> i = getWindows().iterator(); i
                                .hasNext();) {
                            LegacyWindow t = i.next();
                            openWindows += (openWindows.length() > 0 ? "," : "")
                                    + t.getName();
                        }
                        w.showNotification(openWindows);
                    }
                }));

        return w;
    }

    private LegacyWindow createWindow() {
        final LegacyWindow w = new LegacyWindow();
        w.addComponent(new Button("Show the name of the application",
                new Button.ClickListener() {
                    public void buttonClick(ClickEvent event) {
                        w.showNotification("Name of this window = "
                                + w.getName());
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
