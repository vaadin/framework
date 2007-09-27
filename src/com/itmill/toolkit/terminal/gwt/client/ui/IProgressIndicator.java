package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IProgressIndicator extends Widget implements Paintable {
	
	private static final String CLASSNAME = "i-progressindicator";
	Element wrapper = DOM.createDiv();
	Element indicator = DOM.createDiv();
	private ApplicationConnection client;
	private Poller poller;
	
	public IProgressIndicator() {
		setElement(wrapper);
		setStyleName(CLASSNAME);
		DOM.appendChild(wrapper, indicator);
		poller = new Poller();
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		poller.cancel();
		this.client = client;
		if(client.updateComponent(this, uidl, true))
			return;
		boolean indeterminate = uidl.getBooleanAttribute("indeterminate");
		
		if(indeterminate) {
			// TODO put up some image or something
		} else {
			try {
				float f = Float.parseFloat(uidl.getStringAttribute("state"));
				int size = Math.round(100*f);
				DOM.setStyleAttribute(indicator, "width", size + "%");
			} catch (Exception e) {
			}
		}
		poller.scheduleRepeating(uidl.getIntAttribute("pollinginterval"));
	}
	
	class Poller extends Timer {

		public void run() {
			client.sendPendingVariableChanges();
		}
		
	}
	
}
