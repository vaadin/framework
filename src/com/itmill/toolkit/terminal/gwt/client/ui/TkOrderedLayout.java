package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkOrderedLayout extends VerticalPanel implements Paintable {

	public void updateFromUIDL(UIDL uidl, Client client) {
		clear();
		if (uidl.hasAttribute("caption")) setTitle(uidl.getStringAttribute("caption")); 
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL)i.next();
			Widget child = client.createWidgetFromUIDL(uidlForChild);
			add(child);
		}
	}
	
}
