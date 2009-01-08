package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.event.ShortcutAction.KeyCode;
import com.itmill.toolkit.event.ShortcutAction.ModifierKey;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Window;

public class Ticket2431 extends Application {

    @Override
    public void init() {

        Window w = new Window();
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

            public Action[] getActions(Object target, Object sender) {
                return actions;
            }

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
