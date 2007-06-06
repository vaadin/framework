package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class Button extends com.google.gwt.user.client.ui.Button implements Paintable {

	String id;
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		id = uidl.getId();
		setText(uidl.getStringAttribute("caption"));
	}

}
