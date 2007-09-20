package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IPanel extends FlowPanel implements Paintable {
	
	public static final String CLASSNAME = "i-panel";
	
	ApplicationConnection client;
	
	String id;
	
	private Label caption;
	
	private SimplePanel content;
	
	public IPanel() {
		super();
		setStyleName(CLASSNAME);
		caption = new Label();
		caption.setStyleName(CLASSNAME+"-caption");
		content = new SimplePanel();
		content.setStyleName(CLASSNAME+"-content");
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;
		
		this.client = client;
		this.id = uidl.getId();
		
		// TODO optimize: if only the caption has changed, don't re-render whole content
		clear();
		// Remove shadow
		Element deco = DOM.getChild(getElement(), 0);
		if(deco != null)
			DOM.removeChild(getElement(), deco);
		
		if(uidl.hasAttribute("style"))
			setStyleName(CLASSNAME + " " + CLASSNAME+"-"+uidl.getStringAttribute("style"));
		else
			setStyleName(CLASSNAME);
		
		// Handle caption displaying
		if(uidl.hasAttribute("caption") && !uidl.getStringAttribute("caption").equals("")) {
			caption.setText(uidl.getStringAttribute("caption"));
			caption.setStyleName(CLASSNAME+"-caption");
			add(caption);
		} else {
			// Theme needs this to work around different paddings
			caption.setStyleName(CLASSNAME+"-nocaption");
			caption.setText("");
			add(caption);
		}
		
		// Render content
		UIDL layoutUidl = uidl.getChildUIDL(0);
		Widget layout = client.getWidget(layoutUidl);
		content.setWidget(layout);
		add(content);
		((Paintable)layout).updateFromUIDL(layoutUidl, client);
		
		// Add a decoration element for additional styling
		deco = DOM.createDiv();
		DOM.setElementProperty(deco, "className", CLASSNAME+"-deco");
		DOM.appendChild(getElement(), deco);
		
		// Size panel
		String h = uidl.hasVariable("height")? uidl.getStringVariable("height") : null;
		String w = uidl.hasVariable("width")? uidl.getStringVariable("width") : null;
		
		setWidth(w!=null? w : "");
		
		// Try to approximate the height as close as possible
		if(h!=null) {
			// First, calculate needed pixel height
			setHeight(h);
			int neededHeight = getOffsetHeight();
			setHeight("auto");
			// Then calculate the size the content area needs to be
			content.setHeight("0");
			int height = getOffsetHeight();
			content.setHeight(neededHeight-height + "px");
		} else
			content.setHeight("");
		
	}
	
}
