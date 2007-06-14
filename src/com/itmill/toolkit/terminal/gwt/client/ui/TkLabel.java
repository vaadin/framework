package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkLabel extends Label implements Paintable {

	public void updateFromUIDL(UIDL uidl, Client client) {

		if (client.replaceComponentWithCorrectImplementation(this, uidl))
			return;
		client.delegateCaptionToParent(this, uidl);

		try {
			setText(uidl.getChildString(0));
		} catch (Exception e) {
			setText("");

		}
	}
}
