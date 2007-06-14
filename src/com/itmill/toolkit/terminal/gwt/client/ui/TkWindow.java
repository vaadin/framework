package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkWindow extends TkVerticalLayout implements Paintable {
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		super.updateFromUIDL( uidl,  client);
		com.google.gwt.user.client.Window.setTitle(uidl.getStringAttribute("caption"));
	}

}
