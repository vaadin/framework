package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ISlider extends Widget implements Paintable {
	
	/* DOM element for slider's base */
	private Element base;
	
	/* DOM element for slider's handle */
	private Element handle;
	
	public ISlider() {
		super();
		base = DOM.createElement("div");
		handle = DOM.createElement("div");
		DOM.appendChild(base, handle);
		setElement(base);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		
		// Ensure correct implementation (handle own caption)
		if (client.updateComponent(this, uidl, false))
			return;
		
	}

}
