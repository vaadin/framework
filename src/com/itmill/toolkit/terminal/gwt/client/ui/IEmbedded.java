package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IEmbedded extends HTML implements Paintable {

	public void updateFromUIDL(UIDL uidl, Client client) {
		if(uidl.getStringAttribute("type").equals("image")) {
			setHTML("<img src=\""+ uidl.getStringAttribute("src") +"\"/>");
		} else {
			setText("Terminal don't know how ty handle this type of embed");
		}
	}
}
