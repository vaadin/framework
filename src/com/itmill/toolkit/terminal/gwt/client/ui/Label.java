package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class Label extends com.google.gwt.user.client.ui.Label implements Paintable{

	public void updateFromUIDL(UIDL uidl, Client client) {
		setText(uidl.getChildString(0));
	}
}
