/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.book;

import com.vaadin.event.Action;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.Action.Handler;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;

public class DefaultButtonExample extends CustomComponent implements Handler {
	// Define and create user interface components
	Panel panel = new Panel("Login");
	FormLayout formlayout = new FormLayout();
	TextField username = new TextField("Username");
	TextField password = new TextField("Password");
	HorizontalLayout buttons = new HorizontalLayout();

	// Create buttons and define their listener methods.
	Button ok = new Button("OK", this, "okHandler");
	Button cancel = new Button("Cancel", this, "cancelHandler");

	// Have the unmodified Enter key cause an event
	Action action_ok = new ShortcutAction("Default key",
			ShortcutAction.KeyCode.ENTER, null);

	// Have the C key modified with Alt cause an event
	Action action_cancel = new ShortcutAction("Alt+C",
			ShortcutAction.KeyCode.C,
			new int[] { ShortcutAction.ModifierKey.ALT });

	public DefaultButtonExample() {
		// Set up the user interface
		setCompositionRoot(panel);
		panel.addComponent(formlayout);
		formlayout.addComponent(username);
		formlayout.addComponent(password);
		formlayout.addComponent(buttons);
		buttons.addComponent(ok);
		buttons.addComponent(cancel);

		// Set focus to username
		username.focus();

		// Set this object as the action handler
		System.out.println("adding ah");
		panel.addActionHandler(this);

		System.out.println("start done.");
	}

	/**
	 * Retrieve actions for a specific component. This method will be called for
	 * each object that has a handler; in this example just for login panel. The
	 * returned action list might as well be static list.
	 */
	public Action[] getActions(Object target, Object sender) {
		System.out.println("getActions()");
		return new Action[] { action_ok, action_cancel };
	}

	/**
	 * Handle actions received from keyboard. This simply directs the actions to
	 * the same listener methods that are called with ButtonClick events.
	 */
	public void handleAction(Action action, Object sender, Object target) {
		if (action == action_ok) {
			okHandler();
		}
		if (action == action_cancel) {
			cancelHandler();
		}
	}

	public void okHandler() {
		// Do something: report the click
		formlayout.addComponent(new Label("OK clicked. " + "User="
				+ username.getValue() + ", password=" + password.getValue()));
		//  
	}

	public void cancelHandler() {
		// Do something: report the click
		formlayout.addComponent(new Label("Cancel clicked. User="
				+ username.getValue() + ", password=" + password.getValue()));
	}
}
