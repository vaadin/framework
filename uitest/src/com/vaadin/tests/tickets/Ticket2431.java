package com.vaadin.tests.tickets;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;

public class Ticket2431 extends LegacyApplication {

    @Override
    public void init() {

        LegacyWindow w = new LegacyWindow();
        setMainWindow(w);
        Label help = new Label(
                "Use CTRL X to fire action, CTRL C to remove it (fails before fix)");

        w.addComponent(help);

        w.addActionHandler(new Handler() {

            final ShortcutAction a1 = new ShortcutAction("action", KeyCode.X,
                    new int[] { ModifierKey.CTRL });
            final ShortcutAction a2 = new ShortcutAction("action", KeyCode.C,
                    new int[] { ModifierKey.CTRL });

            Action[] actions = new Action[] { a1, a2 };

            @Override
            public Action[] getActions(Object target, Object sender) {
                return actions;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if (action == a1) {
                    getMainWindow().addComponent(new Label("CTRL X hit"));
                } else {
                    actions = new Action[] { a2 };
                    // annoyance, we need to repaint the panel or detect the
                    // action in presence in handler
                    getMainWindow().removeActionHandler(this);
                    getMainWindow().addActionHandler(this);
                }
            }
        });

    }

}
