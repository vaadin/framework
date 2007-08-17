package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventPreview;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * 
 * @author IT Mill Ltd
 */
public class IWindow extends PopupPanel implements Paintable {

	public static final String CLASSNAME = "i-window";

	/** pixels used by borders and paddings horizontally */
	protected static final int BORDER_WIDTH_HORIZONTAL = 2;

	/** pixels used by headers, footers, borders and paddings vertically */
	protected static final int BORDER_WIDTH_VERTICAL = 22;

	private Paintable layout;

	private Element contents;

	private Element header;

	private Element footer;

	private Element resizeBox;

	private boolean dragging;

	private int startX;

	private int startY;

	private int origX;

	private int origY;

	private boolean resizing;

	private int origW;

	private int origH;

	public IWindow() {
		super();
		setStyleName(CLASSNAME);
		constructDOM();
	}
	
	protected void constructDOM() {
		header = DOM.createDiv();
		DOM.setAttribute(header, "className", CLASSNAME + "-header");
		DOM.sinkEvents(header, Event.MOUSEEVENTS);
		contents = DOM.createDiv();
		DOM.setAttribute(contents, "className", CLASSNAME + "-contents");
		footer = DOM.createDiv();
		DOM.setAttribute(footer, "className", CLASSNAME + "-footer");
		resizeBox = DOM.createDiv();
		DOM.setAttribute(resizeBox, "className", CLASSNAME + "-resizeBox");
		DOM.appendChild(footer, resizeBox);
		DOM.sinkEvents(resizeBox, Event.MOUSEEVENTS);
		Element wrapper = getElement();
		DOM.appendChild(wrapper, header);
		DOM.appendChild(wrapper, contents);
		DOM.appendChild(wrapper, footer);
		setPixelSize(400, 200);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		UIDL childUidl = uidl.getChildUIDL(0);
		Paintable lo = (Paintable) client.getWidget(childUidl);
		if (layout != null) {
			if (layout != lo) {
				// remove old
				client.unregisterPaintable(layout);
				this.remove((Widget) layout);
				// add new
				setWidget((Widget) lo);
				layout = lo;
			}
		} else {
			setWidget((Widget) lo);
		}
		if(uidl.hasAttribute("caption")) {
			setCaption(uidl.getStringAttribute("caption"));
		}
		lo.updateFromUIDL(childUidl, client);
	}
	
	public void setCaption(String c) {
		DOM.setInnerHTML(header, c);
	}
	
	public void setPixelSize(int width, int height) {
		// set contents size also due IE's bugs
		DOM.setStyleAttribute(contents, "width", (width - BORDER_WIDTH_HORIZONTAL) + "px");
		DOM.setStyleAttribute(contents, "height", (height - BORDER_WIDTH_VERTICAL) + "px");
		super.setPixelSize(width, height);
	}

	protected Element getContainerElement() {
		return contents;
	}

	public void onBrowserEvent(Event event) {
		Element target = DOM.eventGetTarget(event);
		if (dragging || DOM.compare(header, target))
			onHeaderEvent(event);
		else if (resizing || DOM.compare(resizeBox, target))
			onResizeEvent(event);
	}

	private void onResizeEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
			resizing = true;
			startX = DOM.eventGetScreenX(event);
			startY = DOM.eventGetScreenY(event);
			origW = getOffsetWidth();
			origH = getOffsetHeight();
			DOM.addEventPreview(this);
			break;
		case Event.ONMOUSEUP:
			resizing = false;
			DOM.removeEventPreview(this);
			break;
		case Event.ONMOUSEMOVE:
			if (resizing) {
				int w = DOM.eventGetScreenX(event) - startX + origW;
				if (w < 60)
					w = 60;
				int h = DOM.eventGetScreenY(event) - startY + origH;
				if (h < 60)
					h = 60;
				setPixelSize(w, h);
				DOM.eventPreventDefault(event);
			}
			break;
		default:
			break;
		}
	}

	private void onHeaderEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEDOWN:
			dragging = true;
			startX = DOM.eventGetScreenX(event);
			startY = DOM.eventGetScreenY(event);
			origX = DOM.getAbsoluteLeft(getElement());
			origY = DOM.getAbsoluteTop(getElement());
			DOM.addEventPreview(this);
			break;
		case Event.ONMOUSEUP:
			dragging = false;
			DOM.removeEventPreview(this);
			break;
		case Event.ONMOUSEMOVE:
			if (dragging) {
				int x = DOM.eventGetScreenX(event) - startX + origX;
				int y = DOM.eventGetScreenY(event) - startY + origY;
				this.setPopupPosition(x, y);
				DOM.eventPreventDefault(event);
			}
			break;
		default:
			break;
		}
	}

	public boolean onEventPreview(Event event) {
		if (dragging) {
			onHeaderEvent(event);
			return false;
		} else if (resizing) {
			onResizeEvent(event);
			return false;
		}
		//TODO return false when modal
		return true;
	}

}
