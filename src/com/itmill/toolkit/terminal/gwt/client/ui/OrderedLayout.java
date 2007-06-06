package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class OrderedLayout extends FlowPanel implements Paintable {

	String id;
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		clear();
		id = uidl.getId();
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL)i.next();
		//	Widget child = client.createWidgetsFromUIDL(uidlForChild);
		//	add(child);
		}
	}
	
}
