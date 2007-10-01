package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * "Sub window" component.
 * 
 * TODO update position / scrollposition / size to client
 * 
 * @author IT Mill Ltd
 */
public class IWindow extends PopupPanel implements Paintable {

	private static Vector windowOrder = new Vector();

	public static final String CLASSNAME = "i-window";

	/** pixels used by inner borders and paddings horizontally */
	protected static final int BORDER_WIDTH_HORIZONTAL = 0;

	/** pixels used by headers, footers, inner borders and paddings vertically */
	protected static final int BORDER_WIDTH_VERTICAL = 22;

	private static final int STACKING_OFFSET_PIXELS = 15;

	private static final int Z_INDEX_BASE = 10000;

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

	private Element closeBox;

	private ApplicationConnection client;

	private String id;

	ShortcutActionHandler shortcutHandler;

	/** Last known width read from UIDL or updated to application connection */
	private int uidlWidth = -1;

	/** Last known height read from UIDL or updated to application connection */
	private int uidlHeight = -1;

	/** Last known positionx read from UIDL or updated to application connection */
	private int uidlPositionX = -1;

	/** Last known positiony read from UIDL or updated to application connection */
	private int uidlPositionY = -1;

	public IWindow() {
		super();
		int order = windowOrder.size();
		setWindowOrder(order);
		windowOrder.add(this);
		setStyleName(CLASSNAME);
		constructDOM();
		setPopupPosition(order * STACKING_OFFSET_PIXELS, order
				* STACKING_OFFSET_PIXELS);
	}

	private void bringToFront() {
		int curIndex = windowOrder.indexOf(this);
		if (curIndex + 1 < windowOrder.size()) {
			windowOrder.remove(this);
			windowOrder.add(this);
			for (; curIndex < windowOrder.size(); curIndex++) {
				((IWindow) windowOrder.get(curIndex)).setWindowOrder(curIndex);
			}
		}
	}

	/**
	 * Returns true if window is the topmost window
	 * 
	 * @return
	 */
	private boolean isActive() {
		return windowOrder.lastElement().equals(this);
	}

	public void setWindowOrder(int order) {
		DOM.setStyleAttribute(getElement(), "zIndex", ""
				+ (order + Z_INDEX_BASE));
	}

	protected void constructDOM() {
		header = DOM.createDiv();
		DOM.setElementProperty(header, "className", CLASSNAME + "-header");
		contents = DOM.createDiv();
		DOM.setElementProperty(contents, "className", CLASSNAME + "-contents");
		footer = DOM.createDiv();
		DOM.setElementProperty(footer, "className", CLASSNAME + "-footer");
		resizeBox = DOM.createDiv();
		DOM
				.setElementProperty(resizeBox, "className", CLASSNAME
						+ "-resizebox");
		closeBox = DOM.createDiv();
		DOM.setElementProperty(closeBox, "className", CLASSNAME + "-closebox");
		DOM.appendChild(footer, resizeBox);

		DOM.sinkEvents(header, Event.MOUSEEVENTS);
		DOM.sinkEvents(resizeBox, Event.MOUSEEVENTS);
		DOM.sinkEvents(closeBox, Event.ONCLICK);
		DOM.sinkEvents(contents, Event.ONCLICK);

		Element wrapper = getElement();

		DOM.sinkEvents(wrapper, Event.ONKEYDOWN);

		DOM.appendChild(wrapper, closeBox);
		DOM.appendChild(wrapper, header);
		DOM.appendChild(wrapper, contents);
		DOM.appendChild(wrapper, footer);
		setPixelSize(400, 200);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.id = uidl.getId();
		this.client = client;

		if (uidl.getBooleanAttribute("cached"))
			return;

		if (uidl.hasAttribute("invisible")) {
			this.hide();
			return;
		} else {

			// Initialize the width from UIDL
			try {
				String width = uidl.getStringVariable("width");
				String height = uidl.getStringVariable("width");
				if (width != null && width.endsWith("px")) {
					uidlWidth = Integer.parseInt(width.substring(0, width
							.length() - 2));
					setPixelWidth(uidlWidth);
				}
				if (height != null && height.endsWith("px")) {
					uidlHeight = Integer.parseInt(height.substring(0, height
							.length() - 2));
					setPixelHeight(uidlHeight);
				}
			} catch (IllegalArgumentException e) {
				// Silently ignored as width and height are not required
				// parameters
			}

			// Initialize the position form UIDL
			try {
				int positionx = uidl.getIntVariable("positionx");
				int positiony = uidl.getIntVariable("positiony");
				if (positionx >= 0 && positiony >= 0) {
					setPopupPosition(positionx, positiony);
				}
			} catch (IllegalArgumentException e) {
				// Silently ignored as positionx and positiony are not required
				// parameters
			}

			if (!isAttached()) {
				show();
			}
		}
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
		if (uidl.hasAttribute("caption")) {
			setCaption(uidl.getStringAttribute("caption"));
		}
		lo.updateFromUIDL(childUidl, client);

		// we may have actions
		if (uidl.getChidlCount() > 1) {
			childUidl = uidl.getChildUIDL(1);
			if (childUidl.getTag().equals("actions")) {
				if (shortcutHandler == null)
					shortcutHandler = new ShortcutActionHandler(id, client);
				shortcutHandler.updateActionMap(childUidl);
			}

		}

	}

	public void setPopupPosition(int left, int top) {
		super.setPopupPosition(left, top);
		if (left != uidlPositionX && client != null) {
			client.updateVariable(id, "positionx", left, false);
			uidlPositionX = left;
		}
		if (top != uidlPositionY && client != null) {
			client.updateVariable(id, "positiony", top, false);
			uidlPositionY = top;
		}
	}

	public void setCaption(String c) {
		DOM.setInnerHTML(header, c);
	}

	public void setPixelSize(int width, int height) {
		setPixelHeight(height);
		setPixelWidth(width);
	}

	public void setPixelWidth(int width) {
		DOM.setStyleAttribute(contents, "width",
				(width - BORDER_WIDTH_HORIZONTAL) + "px");
		DOM.setStyleAttribute(header, "width",
				(width - BORDER_WIDTH_HORIZONTAL) + "px");
		DOM.setStyleAttribute(footer, "width",
				(width - BORDER_WIDTH_HORIZONTAL) + "px");
		DOM.setStyleAttribute(getElement(), "width", width + "px");
		if (width != uidlWidth && client != null) {
			client.updateVariable(id, "width", width, false);
			uidlWidth = width;
		}
	}

	public void setPixelHeight(int height) {
		DOM.setStyleAttribute(contents, "height",
				(height - BORDER_WIDTH_VERTICAL) + "px");
		DOM.setStyleAttribute(getElement(), "height", height + "px");
		if (height != uidlHeight && client != null) {
			client.updateVariable(id, "height", height, false);
			uidlHeight = height;
		}

	}

	protected Element getContainerElement() {
		return contents;
	}

	public void onBrowserEvent(Event event) {
		int type = DOM.eventGetType(event);
		if (type == Event.ONKEYDOWN && shortcutHandler != null) {
			int modifiers = KeyboardListenerCollection
					.getKeyboardModifiers(event);
			shortcutHandler.handleKeyboardEvent((char) DOM
					.eventGetKeyCode(event), modifiers);
			return;
		}

		if (!isActive()) {
			bringToFront();
		}
		Element target = DOM.eventGetTarget(event);
		if (dragging || DOM.compare(header, target))
			onHeaderEvent(event);
		else if (resizing || DOM.compare(resizeBox, target))
			onResizeEvent(event);
		else if (DOM.compare(target, closeBox) && type == Event.ONCLICK) {
			onCloseClick();
		}
	}

	private void onCloseClick() {
		client.updateVariable(id, "close", true, true);
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
		// TODO return false when modal
		return true;
	}

}
