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
		DOM.appendChild(getElement(), captionNode);
		DOM.appendChild(getElement(), contentNode);
		DOM.appendChild(getElement(), bottomDecoration);
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
		setHeight(height != null ? height : "");

		// TODO optimize: if only the caption has changed, don't re-render whole
		// content
		if (getWidget() != null) {
			clear();
		}

		// Add proper style name for root element
		// TODO refactor to support additional styles set from server-side
		String className = CLASSNAME;
		if (uidl.hasAttribute("style"))
			className += "-" + uidl.getStringAttribute("style");
		setStyleName(className);
		DOM.setElementProperty(contentNode, "className", className + "-content");
		DOM.setElementProperty(bottomDecoration, "className", className
				+ "-deco");

		// Handle caption displaying
		if (uidl.hasAttribute("caption")
				&& !uidl.getStringAttribute("caption").equals("")) {
			DOM.setInnerText(captionNode, uidl.getStringAttribute("caption"));
			DOM.setElementProperty(captionNode, "className", className
					+ "-caption");
		} else {
			// Theme needs this to work around different styling
			DOM.setElementProperty(captionNode, "className", className
					+ "-nocaption");
			DOM.setInnerText(captionNode, "");
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
			// need to fix containers height properly

			boolean hasChildren = getWidget() != null;
			Element contentEl = null;
			String origPositioning = null;
			if (hasChildren) {
				// remove children temporary form normal flow to detect proper
				// size
				contentEl = getWidget().getElement();
				origPositioning = DOM.getStyleAttribute(contentEl, "position");
				DOM.setStyleAttribute(contentEl, "position", "absolute");
			}
			DOM.setStyleAttribute(contentNode, "height", "");
			int availableH = DOM.getElementPropertyInt(getElement(),
					"clientHeight");

			int usedH = DOM
					.getElementPropertyInt(bottomDecoration, "offsetTop")
					+ DOM.getElementPropertyInt(bottomDecoration,
							"offsetHeight");
			int contentH = availableH - usedH;
			if (contentH < 0)
				contentH = 0;
			DOM.setStyleAttribute(contentNode, "height", contentH + "px");
			if (hasChildren) {
				DOM.setStyleAttribute(contentEl, "position", origPositioning);
			}
			DOM.setStyleAttribute(contentNode, "overflow", "auto");
		} else {
			DOM.setStyleAttribute(contentNode, "overflow", "hidden");
		}
		Util.runAnchestorsLayout(this);
	}

}
