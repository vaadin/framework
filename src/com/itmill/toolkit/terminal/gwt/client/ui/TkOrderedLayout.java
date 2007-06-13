package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkOrderedLayout extends Composite implements Paintable, Layout {

	SimplePanel container = new SimplePanel();

	IndexedPanel panel;

	public TkOrderedLayout() {
		initWidget(container);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		container.clear();

		if ("horizontal".equals(uidl.getStringAttribute("orientation")))
			panel = new HorizontalPanel();
		else
			panel = new VerticalPanel();

		container.add((Panel) panel);

		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL) i.next();
			Widget child = client.createWidgetFromUIDL(uidlForChild);
			((Panel) panel).add(child);
		}
	}

	public void replaceChildComponent(Widget from, Widget to) {
		int index = panel.getWidgetIndex(from);
		if (index >= 0) {
			panel.remove(index);
			if (panel instanceof HorizontalPanel)
				((HorizontalPanel) panel).insert(to, index);
			else
				((VerticalPanel) panel).insert(to, index);
		}
	}

	public boolean hasChildComponent(Widget paintable) {
		return panel.getWidgetIndex(paintable) >= 0;
	}

}
