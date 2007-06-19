package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IWindow extends IVerticalLayout implements Paintable {
	
	private String theme;
	
	public String getTheme() {
		return theme;
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		theme = uidl.getStringAttribute("theme");
		super.updateFromUIDL( uidl,  client);
		com.google.gwt.user.client.Window.setTitle(uidl.getStringAttribute("caption"));
	}

}
