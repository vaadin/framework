package com.vaadin.tests.tickets;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Key codes were converted to lower case on the server (overlapping special key
 * codes for function keys etc.) and then back to upper case on the client.
 * Therefore, registering e.g. F8 as a key code resulted in "w" being used as
 * the trigger and F8 being ignored.
 */
public class Ticket5157 extends LegacyApplication {

    @Override
    public void init() {
        final LegacyWindow mainWindow = new LegacyWindow(
                "Forumtests Application");
        setMainWindow(mainWindow);

        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        Panel p = new Panel(pl);
        mainWindow.addComponent(p);

        Label l = new Label("Panel with F8 bound");
        pl.addComponent(l);

        TextField f = new TextField();
        pl.addComponent(f);

        p.addAction(new ShortcutListener("F8", KeyCode.F8, null) {

            @Override
            public void handleAction(Object sender, Object target) {
                mainWindow.showNotification(getCaption());

            }
        });

        p.addAction(new ShortcutListener("a", KeyCode.A, null) {

            @Override
            public void handleAction(Object sender, Object target) {
                mainWindow.showNotification(getCaption());

            }
        });
    }

}
