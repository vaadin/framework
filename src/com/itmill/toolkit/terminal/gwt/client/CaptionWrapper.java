package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CaptionWrapper extends FlowPanel {

	Caption caption;
	Paintable widget; 
	
	public CaptionWrapper(Paintable toBeWrapped) {
		caption = new Caption(toBeWrapped);
		add(caption);
		widget = toBeWrapped;
		add((Widget) widget);
	}
	
	public void updateCaption(UIDL uidl) {
		caption.updateCaption(uidl);
		setVisible(!uidl.getBooleanAttribute("invisible"));
	}
	
	public Paintable getPaintable() {
		return widget;
	}
}
