package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IEmbedded extends HTML implements Paintable {

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if(uidl.hasAttribute("type") && uidl.getStringAttribute("type").equals("image")) {
			setHTML("<img src=\""+ uidl.getStringAttribute("src") +"\"/>");
		} else {
			setText("Terminal don't know how ty handle this type of embed");
		}
	}
}
