package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class IPanel extends SimplePanel implements Paintable,
		ContainerResizedListener {

	public static final String CLASSNAME = "i-panel";

	ApplicationConnection client;

	String id;

	private Element captionNode = DOM.createDiv();

	private Element bottomDecoration = DOM.createDiv();

	private Element contentNode = DOM.createDiv();
	
	private String height;

	public IPanel() {
		super();
		setStyleName(CLASSNAME);

		DOM.appendChild(getElement(), captionNode);
		DOM
				.setElementProperty(captionNode, "className", CLASSNAME
						+ "-caption");
		DOM.appendChild(getElement(), contentNode);
		DOM
				.setElementProperty(contentNode, "className", CLASSNAME
						+ "-content");
		DOM.appendChild(getElement(), bottomDecoration);
		DOM.setElementProperty(bottomDecoration, "className", CLASSNAME
				+ "-deco");
	}

	protected Element getContainerElement() {
		return contentNode;
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;

		this.client = client;
		this.id = uidl.getId();

		// Panel size. Height needs to be saved for later use
		String w = uidl.hasVariable("width") ? uidl.getStringVariable("width")
				: null;
		height = uidl.hasVariable("height") ? uidl.getStringVariable("height")
				: null;
		setWidth(w != null ? w : "");

		// TODO optimize: if only the caption has changed, don't re-render whole
		// content
		if (getWidget() != null) {
			clear();
		}

		// Add proper style name for root element
		// TODO refactor to support additional styles set from server-side
		if (uidl.hasAttribute("style"))
			setStyleName(CLASSNAME + " " + CLASSNAME + "-"
					+ uidl.getStringAttribute("style"));
		else
			setStyleName(CLASSNAME);

		// Handle caption displaying
		if (uidl.hasAttribute("caption")
				&& !uidl.getStringAttribute("caption").equals("")) {
			DOM.setInnerHTML(captionNode, uidl.getStringAttribute("caption"));
			DOM.setElementProperty(captionNode, "className", CLASSNAME
					+ "-caption");
		} else {
			// Theme needs this to work around different styling
			DOM.setElementProperty(captionNode, "className", CLASSNAME
					+ "-nocaption");
			DOM.setInnerHTML(captionNode, "");
		}
		
		// Height adjustment
		iLayout();
		
		// Render content
		UIDL layoutUidl = uidl.getChildUIDL(0);
		Widget layout = client.getWidget(layoutUidl);
		setWidget(layout);
		((Paintable) layout).updateFromUIDL(layoutUidl, client);

	}

	public void iLayout() {
		// In this case we need to fix containers height properly
		if (height != null && height != "") {
			// First, calculate needed pixel height
			setHeight(height);
			int neededHeight = getOffsetHeight();
			setHeight("");
			// Then calculate the size the content area needs to be
			DOM.setStyleAttribute(contentNode, "height", "0");
			DOM.setStyleAttribute(contentNode, "overflow", "hidden");
			int h = getOffsetHeight();
			int total = neededHeight-h;
			if(total < 0)
				total = 0;
			DOM.setStyleAttribute(contentNode, "height", total + "px");
			DOM.setStyleAttribute(contentNode, "overflow", "");
		} else {
			DOM.setStyleAttribute(contentNode, "height", "");
			// We don't need overflow:auto when height is not set
			DOM.setStyleAttribute(contentNode, "overflow", "hidden");
		}
		Util.runAnchestorsLayout(this);
	}

}
