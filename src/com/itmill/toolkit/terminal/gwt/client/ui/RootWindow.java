	
package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;
import com.itmill.toolkit.terminal.gwt.client.Component;
import com.itmill.toolkit.terminal.gwt.client.GwtClient;

public class RootWindow extends ContainerComponent {

	private RootPanel rp;
	private GwtClient client;

	public RootWindow(Node uidl, GwtClient c) {
		super(0,c);
		// root panel must be attached to client before childs can be rendered
		setClient(c);
		rp = RootPanel.get("itmtk-ajax-window");
		rp.clear();
		renderChildNodes(uidl, client);
	}
	
	void appendChild(Component c) {
		rp.add(c.getWidget());
	}

	public void updateFromUidl(Node n) {
		// TODO Auto-generated method stub

	}

	public Widget getWidget() {
		return rp;
	}

	public void setClient(GwtClient client) {
		this.client = client;
	}
	
	public GwtClient getClient() {
		return this.client;
	}

}
