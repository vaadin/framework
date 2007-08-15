package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Caption extends HTML {
	
	private Paintable owner;

	public Caption(Paintable component)  {
		owner = component;
		setStyleName("i-caption");
	}
	
	public void updateCaption(UIDL uidl) {
		setVisible(!uidl.getBooleanAttribute("invisible"));
		
		String c = uidl.getStringAttribute("caption");
		if (c == null) {
		} else {
			setText(c);
		}
		
		if(uidl.hasAttribute("description")) {
			setTitle(uidl.getStringAttribute("description"));
		}
		
		if(uidl.hasAttribute("error")) {
			// TODO error messages
		}
	}
	
	public static boolean isNeeded(UIDL uidl) {
		if (uidl.getStringAttribute("caption") != null) return true;
		// TODO Description and error messages
		return false;
	}
	
	/**
	 * Returns Paintable for which this Caption
	 * belongs to.
	 * 
	 * @return owner Widget
	 */
	public Paintable getOwner() {
		return owner;
	}
}
