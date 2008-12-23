/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.demo;

import java.util.Date;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.HorizontalLayout;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Panel;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.VerticalLayout;
import com.itmill.toolkit.ui.Window;

/**
 * Test application for KeyboardShortcuts
 */
public class KeyboardShortcut extends Application implements Handler {

    private VerticalLayout loki;

    private final Label instructions = new Label(
            "<p>Keyboard shortcuts is a must have feature for applications in a "
                    + "daily use. In IT Mill toolkit shortcuts are binded to "
                    + "Panel and its subclasses like Windows (most common place)."
                    + "</p>"
                    + "<p>Browsers reserve some keyboard combinations for their own"
                    + " actions, so all combinations cannot be used in web "
                    + "applications. (see our article on <a href=\"http://www.itmill"
                    + ".com/articles/Keybindings_in_Web_Browsers.htm\">"
                    + "www.itmill.com)</a></p>"
                    + "<p>Focus must be inside web application (eg. not in address "
                    + "bar) for shortcuts to work. By default app element is focused.</p>"
                    + "<strong>Shortcuts used in this example:</strong> "
                    + "<br/>ESC restarts program, ctrl-shift-a (Button A), "
                    + "ctrl-shift-z (Button Z), ctrl-shift-x (Button X)",
            Label.CONTENT_XHTML);

    private final Action ACTION_A = new ShortcutAction("Button a action",
            ShortcutAction.KeyCode.A, new int[] {
                    ShortcutAction.ModifierKey.CTRL,
                    ShortcutAction.ModifierKey.SHIFT });

    private final Action ACTION_Z = new ShortcutAction("Button z action",
            ShortcutAction.KeyCode.Z, new int[] {
                    ShortcutAction.ModifierKey.CTRL,
                    ShortcutAction.ModifierKey.SHIFT });

    private final Action ACTION_X = new ShortcutAction("Button x action",
            ShortcutAction.KeyCode.X, new int[] {
                    ShortcutAction.ModifierKey.CTRL,
                    ShortcutAction.ModifierKey.SHIFT });

    private final Action ACTION_RESTART = new ShortcutAction("Restart ",
            ShortcutAction.KeyCode.ESCAPE, null);

    private final Action[] actions = new Action[] { ACTION_A, ACTION_Z,
            ACTION_X, ACTION_RESTART };

    private TextField f;

    public void init() {

        final Window w = new Window("Keyboard shortcuts demo");
        final VerticalLayout main = new VerticalLayout();
        main.setMargin(true);
        main.setSpacing(true);
        setMainWindow(w);
        w.setLayout(main);

        final Panel p = new Panel("Test application for shortcut actions");
        p.addComponent(instructions);

        final HorizontalLayout buttons = new HorizontalLayout();

        // Restart button
        final Button close = new Button("restart", this, "close");
        final Button a = new Button("Button A", this, "actionAHandler");
        final Button z = new Button("Button Z", this, "actionZHandler");
        final Button x = new Button("Button X", this, "actionXHandler");
        f = new TextField();

        buttons.addComponent(close);

        buttons.addComponent(a);
        buttons.addComponent(z);
        buttons.addComponent(x);
        buttons.addComponent(f);
        p.addComponent(buttons);

        main.addComponent(p);

        loki = new VerticalLayout();
        main.addComponent(loki);
        main.setExpandRatio(loki,1.0f);

        w.addActionHandler(this);

    }

    public Action[] getActions(Object target, Object sender) {
        return actions;
    }

    public void handleAction(Action action, Object sender, Object target) {
        if (action == ACTION_A) {
            actionAHandler();
        }
        if (action == ACTION_Z) {
            actionZHandler();
        }
        if (action == ACTION_X) {
            actionXHandler();
        }
        if (action == ACTION_RESTART) {
            actionRestartHandler();
        }
    }

    public void actionAHandler() {
        log("Button A handler fired");
    }

    public void actionZHandler() {
        log("Button Z handler fired");
    }

    public void actionXHandler() {
        log("Button X handler fired");
    }

    public void actionRestartHandler() {
        close();
    }

    public void log(String s) {
        loki.addComponentAsFirst(new Label(new Date() + " : " + s));
    }

}
