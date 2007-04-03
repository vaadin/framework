package com.itmill.toolkit.demo;

import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.*;

public class Shortcut extends com.itmill.toolkit.Application implements Handler {
	Window main;

	Button a;

	Button b;

	Button c;

	Button close;

	Button d;

	private AbstractField f;

	public void init() {

		/*
		 * - Create new window for the application - Give the window a visible
		 * title - Set the window to be the main window of the application
		 */
		main = new Window("Hello window");
		setMainWindow(main);

		// set the application to use Corporate -theme
		setTheme("corporate");

		/*
		 * - Create a label with the classic text - Add the label to the main
		 * window
		 */
		main
				.addComponent(new Label(
						"This is a test program for shortcut actions<br />"
								+ "<b>Note:</b> if events do not work, <b>set focus to Textfield first!</b>",
						Label.CONTENT_XHTML));
		main
				.addComponent(new Label(
						"ESC restarts program, alt-A hits A button, ctrl-B hits B button, ctrl-shift-C hits C"));

		// Restart button
		close = new Button("restart", this, "close");
		close.addActionHandler(this);
		main.addComponent(close);

		a = new Button("Button A", this, "buttonAHandler");
		a.addActionHandler(this);

		b = new Button("Button B", this, "buttonBHandler");
		b.addActionHandler(this);

		c = new Button("Button C", this, "buttonCHandler");
		c.addActionHandler(this);

		f = new TextField("Textfield");

		main.addComponent(a);
		main.addComponent(b);
		main.addComponent(c);
		main.addComponent(f);

		d = new Button("Click to focus button B", this, "setFocusB");
		main.addComponent(d);
		d = new Button("Click to focus Textfield", this, "setFocusF");
		main.addComponent(d);
		f.focus();
	}

	public void setFocusB() {
		b.focus();
	}

	public void setFocusF() {
		f.focus();
	}

	public Action[] getActions(Object target, Object sender) {
		Action[] actions = new Action[1];
		if (sender == b) {
			actions[0] = (Action) (new ShortcutAction("Button b action",
					ShortcutAction.KeyCode.B,
					new int[] { ShortcutAction.ModifierKey.CTRL }));

		} else if (sender == c) {
			actions[0] = (Action) new ShortcutAction("Button c action",
					ShortcutAction.KeyCode.C, new int[] {
							ShortcutAction.ModifierKey.CTRL,
							ShortcutAction.ModifierKey.SHIFT });
		} else if (sender == a) {
			actions[0] = (Action) new ShortcutAction("Button a action",
					ShortcutAction.KeyCode.A,
					new int[] { ShortcutAction.ModifierKey.ALT });
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
		if (target == b)
			this.buttonBHandler();
		if (target == c)
			this.buttonCHandler();
		if (target == close)
			this.close();
	}

	public void buttonAHandler() {
		main.addComponent(new Label("Button A handler fired"));
	}

	public void buttonBHandler() {
		main.addComponent(new Label("Button B handler fired"));
	}

	public void buttonCHandler() {
		main.addComponent(new Label("Button C handler fired"));
	}
}
