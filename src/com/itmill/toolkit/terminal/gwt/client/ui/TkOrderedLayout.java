package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkOrderedLayout extends Composite implements Paintable {
	
	SimplePanel container = new SimplePanel();
	
	Panel panel;
	
	public TkOrderedLayout() {
		initWidget(container);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		container.clear();
		
		if(uidl.getStringAttribute("orientation").equals("horizontal"))
			panel = new HorizontalPanel();
		else
			panel = new VerticalPanel();
		
		container.add(panel);
		
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL)i.next();
			Widget child = client.createWidgetFromUIDL(uidlForChild);
			panel.add(child);
		}
	}
	
}
