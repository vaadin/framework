package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class Button extends com.google.gwt.user.client.ui.Button implements
		Paintable, ClickListener {

	String id;

	Client client;

	public Button() {
		addClickListener(this);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		id = uidl.getId();
		setText(uidl.getStringAttribute("caption"));
	}

	public void onClick(Widget sender) {
		if (id == null || client == null)
			return;
		client.updateVariable(id, "state", true, true);
	}
}
