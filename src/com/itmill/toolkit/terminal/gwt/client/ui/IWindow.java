package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.KeyboardListenerCollection;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollListener;
import com.google.gwt.user.client.ui.ScrollPanel;
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
public class IWindow extends PopupPanel implements Paintable, ScrollListener {

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

	private ScrollPanel contentPanel = new ScrollPanel();

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
		contentPanel.addScrollListener(this);
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
		setWidget(contentPanel);

		// set default size
		setWidth(400 + "px");
		setHeight(300 + "px");
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.id = uidl.getId();
		this.client = client;
		
		if(client.updateComponent(this, uidl, false))
			return;

		if (uidl.hasAttribute("invisible")) {
			this.hide();
			return;
		} else {

			// Initialize the width from UIDL
			if (uidl.hasVariable("width")) {
				String width = uidl.getStringVariable("width");
				setWidth(width);
			}
			if (uidl.hasVariable("height")) {
				String height = uidl.getStringVariable("height");
				setHeight(height);
			}

			contentPanel.setScrollPosition(uidl.getIntVariable("scrolltop"));
			contentPanel.setHorizontalScrollPosition(uidl
					.getIntVariable("scrollleft"));

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
				contentPanel.remove((Widget) layout);
				// add new
				contentPanel.setWidget((Widget) lo);
				layout = lo;
			}
		} else {
			contentPanel.setWidget((Widget) lo);
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
			setSize(event, true);
			break;
		case Event.ONMOUSEMOVE:
			if (resizing) {
				setSize(event, false);
				DOM.eventPreventDefault(event);
			}
			break;
		default:
			break;
		}
	}

	public void setSize(Event event, boolean updateVariables) {
		int w = DOM.eventGetScreenX(event) - startX + origW;
		if (w < 60)
			w = 60;
		int h = DOM.eventGetScreenY(event) - startY + origH;
		if (h < 60)
			h = 60;
		setWidth(w + "px");
		setHeight(h + "px");
		if (updateVariables) {
			// sending width back always as pixels, no need for unit
			client.updateVariable(id, "width", w, false);
			client.updateVariable(id, "height", h, false);
		}
	}

	public void setWidth(String width) {
		super.setWidth(width);
		DOM.setStyleAttribute(header, "width", width);
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

	public void onScroll(Widget widget, int scrollLeft, int scrollTop) {
		client.updateVariable(id, "scrolltop", scrollTop, false);
		client.updateVariable(id, "scrollleft", scrollLeft, false);
	}

}
