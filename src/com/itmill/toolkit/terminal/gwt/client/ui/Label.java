package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class Label extends Composite implements Paintable{
	
	com.google.gwt.user.client.ui.Label caption = new com.google.gwt.user.client.ui.Label();;
	com.google.gwt.user.client.ui.Label content = new com.google.gwt.user.client.ui.Label();;
	
	public Label() {
		VerticalPanel panel = new VerticalPanel();
		panel.add(caption);
		panel.add(content);
		initWidget(panel);
		setStyleName("itmtk-label");
		caption.setStyleName("itmtk-label-caption");
		content.setStyleName("itmtk-label-content");
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		setContent(uidl.getChildString(0));
		if(uidl.hasAttribute("caption"))
			setCaption(uidl.getStringAttribute("caption"));
		else
			caption.setVisible(false);
	}
	
	public void setContent(String c) {
		content.setText(c);
	}
	public void setCaption(String c) {
		caption.setText(c);
	}
}
