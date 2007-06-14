package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkHorizontalLayout extends HorizontalPanel implements Paintable, Layout {

	public void updateFromUIDL(UIDL uidl, Client client) {
		
//		 Ensure correct implementation
		if (client.replaceComponentWithCorrectImplementation(this, uidl))
			return;

		// TODO Should update instead of just redraw
		clear();
		
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL) i.next();
			Widget child = client.createWidgetFromUIDL(uidlForChild);
			add(child);
		}
	}

	public void replaceChildComponent(Widget from, Widget to) {
		int index = getWidgetIndex(from);
		if (index >= 0) {
			remove(index);
			insert(to, index);
		}
	}

	public boolean hasChildComponent(Widget paintable) {
		return getWidgetIndex(paintable) >= 0;
	}

	public boolean updateCaption(Widget component, UIDL uidl) {
		
		if (!hasChildComponent(component) || uidl == null) return false;
		
		
		return false;
	}

}
