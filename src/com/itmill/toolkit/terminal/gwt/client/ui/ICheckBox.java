package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICheckBox extends com.google.gwt.user.client.ui.CheckBox implements
		Paintable {

	public static final String CLASSNAME = "i-checkbox";

	String id;

	boolean immediate;

	ApplicationConnection client;

	public ICheckBox() {
		setStyleName(CLASSNAME);
		addClickListener(new ClickListener() {

			public void onClick(Widget sender) {
				if (id == null || client == null)
					return;
				client.updateVariable(id, "state", isChecked(), immediate);
			}

		});
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;

		// Save details
		this.client = client;
		id = uidl.getId();

		// Set text
		setText(uidl.getStringAttribute("caption"));
		setChecked(uidl.getBooleanVariable("state"));
		immediate = uidl.getBooleanAttribute("immediate");
	}

}
