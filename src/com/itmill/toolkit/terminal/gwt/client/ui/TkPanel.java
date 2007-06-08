package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkPanel extends Composite implements Paintable {

	Panel p = new VerticalPanel();
	Label caption = new Label();

	public TkPanel() {
		p.add(caption);
		initWidget(p);
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		p.clear();
		p.add(caption);
		
		if(uidl.hasAttribute("caption"));
		caption.setText(uidl.getStringAttribute("caption"));
		
		Iterator it = uidl.getChildIterator();
		while(it.hasNext())
			p.add(client.createWidgetFromUIDL((UIDL) it.next()));
	}
}
