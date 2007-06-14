package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkLabel extends HTML implements Paintable {

	public void updateFromUIDL(UIDL uidl, Client client) {

		if (client.replaceComponentWithCorrectImplementation(this, uidl))
			return;
		client.delegateCaptionToParent(this, uidl);

		String mode = uidl.getStringAttribute("mode");
		if (mode == null || "text".equals(mode))
			setText(uidl.getChildString(0));
		else if ("pre".equals(mode)) {
			setHTML(uidl.getChildrenAsXML());
		} else if ("uidl".equals(mode)) {
			setHTML(uidl.getChildrenAsXML());
		} else if ("xhtml".equals(mode)) {
			setHTML(uidl.getChildUIDL(0).getChildUIDL(0).getChildString(0));
		} else if ("xml".equals(mode)) {
			setHTML(uidl.getChildUIDL(0).getChildString(0));
		} else if ("raw".equals(mode)) {
			setHTML(uidl.getChildUIDL(0).getChildString(0));
		} else {
			setText("");
		}
	}
}
