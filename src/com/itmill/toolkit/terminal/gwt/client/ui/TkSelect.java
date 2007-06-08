package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkSelect extends Composite implements Paintable {
	
	Label caption = new Label();
	ListBox select = new ListBox();
	
	public TkSelect() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(caption);
		panel.add(select);
		initWidget(panel);
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		
		if (uidl.hasAttribute("caption")) caption.setText(uidl.getStringAttribute("caption")); 

		UIDL options = uidl.getChildUIDL(0);
		
		for (Iterator i = options.getChildIterator(); i.hasNext();) {
			UIDL optionUidl = (UIDL)i.next();
			select.addItem(optionUidl.getStringAttribute("caption"), optionUidl.getStringAttribute("key"));
		}
	}
}
