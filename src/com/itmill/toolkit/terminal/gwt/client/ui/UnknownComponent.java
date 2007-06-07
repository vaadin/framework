package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class UnknownComponent extends Composite implements Paintable{
	
	com.google.gwt.user.client.ui.Label caption = new com.google.gwt.user.client.ui.Label();;
	Tree uidlTree = new Tree();
	
	public UnknownComponent() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(caption);
		panel.add(uidlTree);
		initWidget(panel);
		setStyleName("itmtk-unknown");
		caption.setStyleName("itmtk-unknown-caption");
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		setCaption("Client faced an unknown component type. Unrendered UIDL:");
		uidlTree.addItem(uidl.dir());
	}
	
	public void setCaption(String c) {
		caption.setText(c);
	}
}
