package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
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

		// Size panel
		String h = uidl.hasVariable("height") ? uidl
				.getStringVariable("height") : null;
		String w = uidl.hasVariable("width") ? uidl.getStringVariable("width")
				: null;

		setWidth(w != null ? w : "");

		if (h != null) {
			setHeight(h);
		} else {
			DOM.setStyleAttribute(contentNode, "height", "");
			// We don't need overflow:auto when panel height is not set
			// (overflow:auto causes rendering errors at least in Firefox when a
			// a panel is inside a tabsheet with overflow:auto set)
			DOM.setStyleAttribute(contentNode, "overflow", "hidden");
		}

		// TODO optimize: if only the caption has changed, don't re-render whole
		// content
		if(getWidget() != null) {
			clear();
		}

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
			// Theme needs this to work around different paddings
			DOM.setElementProperty(captionNode, "className", CLASSNAME
					+ "-nocaption");
			DOM.setInnerHTML(captionNode, "");
		}
		
		iLayout();

		// Render content
		UIDL layoutUidl = uidl.getChildUIDL(0);
		Widget layout = client.getWidget(layoutUidl);
		setWidget(layout);
		((Paintable) layout).updateFromUIDL(layoutUidl, client);

	}

	public void iLayout() {
		String h = DOM.getStyleAttribute(getElement(), "height");
		if (h != null && h != "") {
			// need to fix containers height properly

			boolean hasChildren = getWidget() != null;
			Element contentEl = null;
			String  origPositioning = null;
			if(hasChildren) {
				// remove children temporary form normal flow to detect proper size
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
			if(contentH < 0)
				contentH = 0;
			DOM.setStyleAttribute(contentNode, "height", contentH + "px");
			if(hasChildren) {
				DOM.setStyleAttribute(contentEl, "position", origPositioning);
			}
		}
		Util.runAnchestorsLayout(this);
	}

}
