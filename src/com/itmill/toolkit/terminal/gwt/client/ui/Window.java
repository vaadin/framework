package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class Window extends FlowPanel implements Paintable {

	String id;

	public Window() {
		super();
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {

		id = uidl.getId();
		com.google.gwt.user.client.Window.setTitle(uidl.getStringAttribute("caption"));
		UIDL child = uidl.getChildUIDL(0);
		add(client.createWidgetFromUIDL(child));
	}

}
