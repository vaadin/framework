package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;

public class TkOrderedLayout extends ContainerComponent {
	
	private FlowPanel p;

	public TkOrderedLayout(Node uidl, GwtClient cli) {
		super(getIdFromUidl(uidl), cli);

		p = new FlowPanel();
		updateFromUidl(uidl);
	}

	void appendChild(Component c) {
		p.add(c.getWidget());
	}

	public Widget getWidget() {
		return p;
	}

	public void updateFromUidl(Node n) {
		p.clear();
		renderChildNodes(n, getClient());
	}

}
