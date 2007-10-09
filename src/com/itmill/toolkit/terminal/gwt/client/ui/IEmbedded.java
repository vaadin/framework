package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IEmbedded extends HTML implements Paintable {

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		if (client.updateComponent(this, uidl, true))
			return;

		if (uidl.hasAttribute("type")
				&& uidl.getStringAttribute("type").equals("image")) {
			setHTML("<img src=\"" + uidl.getStringAttribute("src") + "\"/>");
		} else if (uidl.hasAttribute("mimetype")
				&& uidl.getStringAttribute("mimetype").equals(
						"application/x-shockwave-flash")) {
			String w = uidl.hasAttribute("width") ? uidl
					.getStringAttribute("width") : "100";
			String h = uidl.hasAttribute("height") ? uidl
					.getStringAttribute("height") : "100";

			setHTML("<object width=\"" + w + "\" height=\"" + h
					+ "\"><param name=\"movie\" value=\""
					+ uidl.getStringAttribute("src") + "\"><embed src=\""
					+ uidl.getStringAttribute("src") + "\" width=\"" + w
					+ "\" height=\"" + h + "\"></embed></object>");
		} else {
			setText("Terminal don't know how ty handle this type of embed");
		}
	}
}
