package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IComponent extends IPanel {
	
	public IComponent() {
		super();
		setStyleName("i-component");
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		super.updateFromUIDL(uidl, client);
		setStyleName("i-component");
	}

}
