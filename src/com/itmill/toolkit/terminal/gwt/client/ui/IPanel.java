package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IPanel extends FlowPanel implements Paintable {
	
	private static final String CLASSNAME = "i-panel";
	
	Client client;
	
	String id;
	
	private Label caption;
	
	public IPanel() {
		super();
		setStyleName(CLASSNAME);
		caption = new Label();
		caption.setStyleName(CLASSNAME+"-caption");
	}

	public void updateFromUIDL(UIDL uidl, Client client) {
		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;
		
		this.client = client;
		this.id = uidl.getId();
		
		if(uidl.hasAttribute("caption"))
			caption.setText(uidl.getStringAttribute("caption"));
		else
			caption.setText("");
		
		if(uidl.hasAttribute("style"))
			setStyleName(CLASSNAME + " " + CLASSNAME+"-"+uidl.getStringAttribute("style"));
		else
			setStyleName(CLASSNAME);
		
		clear();
		add(caption);
		
		UIDL layoutUidl = uidl.getChildUIDL(0);
		Widget layout = client.getWidget(layoutUidl);
		((Paintable)layout).updateFromUIDL(layoutUidl, client);
		add(layout);
		
	}
	
}
