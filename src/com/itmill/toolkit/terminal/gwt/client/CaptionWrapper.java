package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CaptionWrapper extends VerticalPanel {

	Label caption;
	Widget widget; 
	
	public CaptionWrapper(Widget toBeWrapped) {
		widget = toBeWrapped;
		add(widget);
	}
	
	public void updateCaption(UIDL uidl) {
		String c = uidl.getStringAttribute("caption");
		// TODO Description and error messages
		if (c == null) {
			if (caption == null) return;
			remove(caption);
			caption = null;
		} else {
			if (caption == null) {
				caption = new Label(c);
				insert(caption, 0);
			}
			else 
				caption.setText(c);
		}		
		setVisible(!uidl.getBooleanAttribute("invisible"));
	}
	
	public static boolean isNeeded(UIDL uidl) {
		if (uidl.getStringAttribute("caption") != null) return true;
		// TODO Description and error messages
		return false;
	}
	
	public Widget getWidget() {
		return widget;
	}
}
