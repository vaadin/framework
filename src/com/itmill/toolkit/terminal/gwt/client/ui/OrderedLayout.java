package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class OrderedLayout extends FlowPanel implements Paintable {

	public void updateFromUIDL(UIDL uidl, Client client) {
		clear();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL)i.next();
			Widget child = client.createWidgetFromUIDL(uidlForChild);
			add(child);
		}
	}
	
}
