package com.itmill.toolkit.tests.magi;
import com.itmill.toolkit.event.Action;
import com.itmill.toolkit.event.ShortcutAction;
import com.itmill.toolkit.event.Action.Handler;
import com.itmill.toolkit.ui.*;

public class DefaultButtonExample extends CustomComponent implements Handler {
	// Define and create user interface components
	Panel         panel      = new Panel("Login");
	OrderedLayout formlayout = new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL);
	TextField     username   = new TextField("Username");
	TextField     password   = new TextField("Password");
	OrderedLayout buttons    = new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL);
	
	// Create buttons and define their listener methods. Here we use parameterless
	// methods so that we can use same methods for both click events and keyboard
	// actions.
	Button        ok         = new Button("OK", this, "okHandler");
	Button        cancel     = new Button("Cancel", this, "cancelHandler");

	public DefaultButtonExample() {
		// Set up the user interface
		setCompositionRoot(panel);
		panel.addComponent(formlayout);
		formlayout.addComponent(username);
		formlayout.addComponent(password);
		formlayout.setStyle("form");
		formlayout.addComponent(buttons);
		buttons.addComponent(ok);
		buttons.addComponent(cancel);
		
		// Set focus to username
		username.focus();
		
		// Set this object as the action handler for actions related to the Ok
		// and Cancel buttons.
		// @TODO
		//ok.addActionHandler(this);
		//cancel.addActionHandler(this);
	}

	/**
	 * Retrieve actions for a specific component. This method will be called for each
	 * object that has a handler; in this example the Ok and Cancel buttons.
	 **/
	public Action[] getActions(Object target, Object sender) {
		Action[] actions = new Action[1];

		// Set the action for the requested component 
        if (sender == ok) {
        	// Bind the unmodified Enter key to the Ok button. 
            actions[0] = new ShortcutAction("Default key",
                                            ShortcutAction.KeyCode.ENTER, null);
        } else if (sender == cancel) {
        	// Bind "C" key modified with Alt to the Cancel button.
            actions[0] = new ShortcutAction("Alt+C",
                                            ShortcutAction.KeyCode.C, new int[] {
                                               	ShortcutAction.ModifierKey.ALT});
        } else
        	return null;
		return actions;
	}

	/**
	 * Handle actions received from keyboard. This simply directs the actions to
	 * the same listener methods that are called with ButtonClick events.
	 **/
	public void handleAction(Action action, Object sender, Object target) {
		if (target == ok)
			this.okHandler();
		if (target == cancel)
			this.cancelHandler();
	}

	public void okHandler() {
		// Do something: report the click
		formlayout.addComponent(new Label("OK clicked"));
	}

	public void cancelHandler() {
		// Do something: report the click
		formlayout.addComponent(new Label("Cancel clicked"));
	}
}
