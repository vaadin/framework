package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class GridLayout extends VerticalPanel implements Paintable {

	public void updateFromUIDL(UIDL uidl, Client client) {
		clear();
		if (uidl.hasAttribute("caption")) setTitle(uidl.getStringAttribute("caption"));
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL r = (UIDL) i.next();
			if ("gr".equals(r.getTag())) { 
				HorizontalPanel row = new HorizontalPanel();
				add(row);
				for (Iterator j = r.getChildIterator(); j.hasNext();) {
					UIDL c = (UIDL) j.next();
					if ("gc".equals(c.getTag())) {
						UIDL u = c.getChildUIDL(0);						
						Widget child = client.createWidgetFromUIDL(u);
						if (child != null)
							row.add(child);
					}
				}
			}
		}
	}
	
}
