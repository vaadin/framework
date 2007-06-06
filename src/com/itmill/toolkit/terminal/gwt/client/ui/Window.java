package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class Window extends DockPanel implements Paintable {

	String id;
	Label caption = new Label();

	public Window() {
		super();
		setBorderWidth(2);
		add(caption,NORTH);
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {

		id = uidl.getId();
		caption.setText(uidl.getStringAttribute("caption"));
		UIDL child = uidl.getChildUIDL(0);
		add(client.createWidgetFromUIDL(child),CENTER);
	}

}
