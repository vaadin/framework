package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IForm extends SimplePanel implements Paintable {
	
	public static final String CLASSNAME = "i-form";
	
	private Layout  lo;

	private ApplicationConnection client;
	
	public IForm() {
		super();
		setStyleName("CLASSNAME");
	}
	
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.client = client;
		UIDL layoutUidl = uidl.getChildUIDL(0);
		if(lo == null) {
			lo = (Layout) client.getWidget(layoutUidl);
			setWidget((Widget) lo);
		}
		lo.updateFromUIDL(layoutUidl, client);
	}
}

