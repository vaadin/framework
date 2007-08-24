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
		
		// Size panel
		// TODO support for different units
		String widthUnit = "px";
		String heightUnit = "px";
		int captionHeight = caption.getOffsetHeight();
		int height = uidl.hasVariable("height")? uidl.getIntVariable("height") : -1;
		int w = uidl.hasVariable("width")? uidl.getIntVariable("width") : -1;
		int h = -1;
		if(height != -1) 
			h = height < captionHeight? 0 : height - captionHeight;
		setWidth(w>=0?w+widthUnit:"auto");
		content.setHeight(h>=0?h+heightUnit:"auto");
		
		// Render content
		UIDL layoutUidl = uidl.getChildUIDL(0);
		Widget layout = client.getWidget(layoutUidl);
		((Paintable)layout).updateFromUIDL(layoutUidl, client);
		content.setWidget(layout);
		
		add(content);
		
		// Add a decoration element for shadow
		deco = DOM.createDiv();
		DOM.setElementProperty(deco, "className", CLASSNAME+"-deco");
		DOM.appendChild(getElement(), deco);
		
	}
	
}
