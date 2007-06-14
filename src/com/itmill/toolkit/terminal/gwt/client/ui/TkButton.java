package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkButton extends com.google.gwt.user.client.ui.Button implements
		Paintable {

	String id;

	Client client;

	public TkButton() {
		addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				if (id == null || client == null)
					return;
				client.updateVariable(id, "state", true, true);
			}
		});
	}

	public void updateFromUIDL(UIDL uidl, Client client) {

		// Ensure correct implementation
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
