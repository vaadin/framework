package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkTabsheet extends TabPanel implements
		Paintable {

	String id;

	Client client;

	public TkTabsheet() {
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		this.client = client;
		id = uidl.getId();
		
		UIDL tabs = uidl.getChildUIDL(0);
		for(Iterator it = tabs.getChildIterator(); it.hasNext();) {
			UIDL tab = (UIDL) it.next();
			if(tab.getBooleanAttribute("selected")) {
				Widget content = client.createWidgetFromUIDL(tab.getChildUIDL(0));
				this.add(content, tab.getStringAttribute("caption"));
				this.selectTab(this.getWidgetIndex(content));
			} else {
				this.add(new Label(), tab.getStringAttribute("caption"));
			}
		}
		
	}

}
