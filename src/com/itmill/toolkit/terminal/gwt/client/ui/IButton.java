package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Button;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IButton extends Button implements Paintable {
	
	public static final String CLASSNAME = "i-button";

	String id;

	ApplicationConnection client;

	public IButton() {
		setStyleName(CLASSNAME);
		addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				if (id == null || client == null)
					return;
				client.updateVariable(id, "state", true, true);
			}
		});
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

		// Ensure correct implementation,
		// but don't let container manage caption etc.
		if (client.updateComponent(this, uidl, false))
			return;

		// Save details
		this.client = client;
		id = uidl.getId();

		// Set text
		setText(uidl.getStringAttribute("caption"));

		// TODO Handle description and errormessages
	}

}
