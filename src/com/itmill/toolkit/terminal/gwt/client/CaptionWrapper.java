package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CaptionWrapper extends FlowPanel {

	Label caption = new Label();
	Paintable widget; 
	
	public CaptionWrapper(Paintable toBeWrapped) {
		add(caption);
		widget = toBeWrapped;
		add((Widget) widget);
	}
	
	public void updateCaption(UIDL uidl) {
		String c = uidl.getStringAttribute("caption");
		// TODO Description and error messages
		if (c != null) {
			caption.setText(c);
		}
		setVisible(!uidl.getBooleanAttribute("invisible"));
	}
	
	public Paintable getPaintable() {
		return widget;
	}
}
