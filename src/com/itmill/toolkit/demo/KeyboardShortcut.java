package com.itmill.toolkit.demo;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.*;

/**
 * Note: This feature is under development and is considered as beta
 * 
 * @author IT Mill Ltd.
 * 
 */
public class KeyboardShortcut extends com.itmill.toolkit.Application implements
		Handler {
	private Window main;

	private Button a;

	private Button z;

	private Button x;

	private Button close;

	private AbstractField f;

	public void init() {

		main = new Window("Keyboard shortcuts demo");
		setMainWindow(main);
		setTheme("corporate");

		main
				.addComponent(new Label(
						"<h3>Test application for shortcut actions</h3>"
								+ "<p><b>Notes:</b><br />"
								+ "<b>This feature is under development and it's API may still change.</b><br />"
								+ "<b>If events do not work, <b>set focus to Textfield first.</b><br />"
								+ "<b>Browsers may have reserved the keyboard combinations used in "
								+ "this demo for other purposes.</b><br /></p>",
						Label.CONTENT_RAW));
		main
				.addComponent(new Label(
						"ESC restarts program, ctrl-shift-a clicks A button, "
								+ "ctrl-shift-z clicks Z button, ctrl-shift-x clicks X button"));

		// Restart button
		close = new Button("restart", this, "close");
		close.addActionHandler(this);
		main.addComponent(close);

		a = new Button("Button A", this, "buttonAHandler");
		a.addActionHandler(this);

		z = new Button("Button Z", this, "buttonZHandler");
		z.addActionHandler(this);

		x = new Button("Button X", this, "buttonXHandler");
		x.addActionHandler(this);

		f = new TextField("Textfield");

		main.addComponent(a);
		main.addComponent(z);
		main.addComponent(x);
		main.addComponent(f);

		f.focus();
	}

	public Action[] getActions(Object target, Object sender) {
		Action[] actions = new Action[1];
		if (sender == a) {
			actions[0] = (Action) new ShortcutAction("Button a action",
					ShortcutAction.KeyCode.A, new int[] {
							ShortcutAction.ModifierKey.CTRL,
							ShortcutAction.ModifierKey.SHIFT });
		} else if (sender == z) {
			actions[0] = (Action) (new ShortcutAction("Button z action",
					ShortcutAction.KeyCode.Z, new int[] {
							ShortcutAction.ModifierKey.CTRL,
							ShortcutAction.ModifierKey.SHIFT }));

		} else if (sender == x) {
			actions[0] = (Action) new ShortcutAction("Button x action",
					ShortcutAction.KeyCode.X, new int[] {
							ShortcutAction.ModifierKey.CTRL,
							ShortcutAction.ModifierKey.SHIFT });
		} else {
			// restart button
			actions[0] = new ShortcutAction("Restart ",
					ShortcutAction.KeyCode.ESCAPE, null);
		}
		return actions;
	}

	public void handleAction(Action action, Object sender, Object target) {
		if (target == a)
			this.buttonAHandler();
		if (target == z)
			this.buttonZHandler();
		if (target == x)
			this.buttonXHandler();
		if (target == close)
			this.close();
	}

	public void buttonAHandler() {
		main.addComponent(new Label("Button A handler fired"));
	}

	public void buttonZHandler() {
		main.addComponent(new Label("Button Z handler fired"));
	}

	public void buttonXHandler() {
		main.addComponent(new Label("Button X handler fired"));
	}

}
