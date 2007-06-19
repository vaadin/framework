package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ILabel extends HTML implements Paintable {
	
	public static final String CLASSNAME = "i-label";

	public void updateFromUIDL(UIDL uidl, Client client) {
		
		setStyleName(CLASSNAME);

		if (client.updateComponent(this, uidl, true))
			return;

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
