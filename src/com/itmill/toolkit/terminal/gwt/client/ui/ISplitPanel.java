package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

public class ISplitPanel extends ComplexPanel implements Paintable,
		ContainerResizedListener {
	public static final String CLASSNAME = "i-splitpanel";

	public static final int ORIENTATION_HORIZONTAL = 0;
	public static final int ORIENTATION_VERTICAL = 1;

	private static final int SPLITTER_SIZE = 10;

	private static final String MIN_SIZE = (3 * SPLITTER_SIZE) + "px";

	private int orientation;
	private Widget firstChild;
	private Widget secondChild;

	private Element wrapper = DOM.createDiv();
	private Element firstContainer = DOM.createDiv();
	private Element secondContainer = DOM.createDiv();
	private Element splitter = DOM.createDiv();

	private boolean resizing;

	private int origX;

	private int origY;

	private int origMouseX;

	private int origMouseY;

	public ISplitPanel() {
		this(ORIENTATION_HORIZONTAL);
	}

	public ISplitPanel(int orientation) {
		setElement(DOM.createDiv());
		switch (orientation) {
		case ORIENTATION_HORIZONTAL:
			setStyleName(CLASSNAME + "-horizontal");
			break;
		case ORIENTATION_VERTICAL:
		default:
			setStyleName(CLASSNAME + "-vertical");
			break;
		}
		// size below will be overridden in update from uidl, initial size
		// needed to keep IE alive
		setWidth(MIN_SIZE);
		setHeight(MIN_SIZE);
		constructDom();
		setOrientation(orientation);
		setSplitPosition("50%");
		DOM.sinkEvents(splitter, (Event.MOUSEEVENTS));
		DOM.sinkEvents(getElement(), (Event.MOUSEEVENTS));
	}

	protected void constructDom() {
		DOM.appendChild(getElement(), wrapper);
		DOM.setStyleAttribute(wrapper, "position", "relative");
		DOM.setStyleAttribute(wrapper, "width", "100%");
		DOM.setStyleAttribute(wrapper, "height", "100%");

		DOM.appendChild(wrapper, splitter);
		DOM.appendChild(wrapper, secondContainer);
		DOM.appendChild(wrapper, firstContainer);

		DOM.setStyleAttribute(splitter, "position", "absolute");
		DOM.setStyleAttribute(secondContainer, "position", "absolute");
		DOM.setElementProperty(splitter, "className", "splitter");

		DOM.setStyleAttribute(firstContainer, "overflow", "hidden");
		DOM.setStyleAttribute(secondContainer, "overflow", "hidden");

	}

	private void setOrientation(int orientation) {
		this.orientation = orientation;
		if (orientation == ORIENTATION_HORIZONTAL) {
			DOM.setStyleAttribute(splitter, "height", "100%");
			DOM.setStyleAttribute(splitter, "width", SPLITTER_SIZE + "px");
			DOM.setStyleAttribute(firstContainer, "height", "100%");
			DOM.setStyleAttribute(secondContainer, "height", "100%");
		} else {
			DOM.setStyleAttribute(splitter, "width", "100%");
			DOM.setStyleAttribute(splitter, "height", SPLITTER_SIZE + "px");
			DOM.setStyleAttribute(firstContainer, "width", "100%");
			DOM.setStyleAttribute(secondContainer, "width", "100%");
		}
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		client.updateComponent(this, uidl, true);

		setWidth(uidl.getStringAttribute("width"));
		setHeight(uidl.getStringAttribute("height"));

		setSplitPosition(uidl.getStringAttribute("position"));

		Paintable newFirstChild = (Paintable) client.getWidget(uidl
				.getChildUIDL(0));
		Paintable newSecondChild = (Paintable) client.getWidget(uidl
				.getChildUIDL(1));
		if (firstChild != newFirstChild) {
			if (firstChild != null)
				client.unregisterPaintable((Paintable) firstChild);
			setFirstWidget((Widget) newFirstChild);
		}
		if (secondChild != newSecondChild) {
			if (secondChild != null)
				client.unregisterPaintable((Paintable) secondChild);
			setSecondWidget((Widget) newSecondChild);
		}
		newFirstChild.updateFromUIDL(uidl.getChildUIDL(0), client);
		newSecondChild.updateFromUIDL(uidl.getChildUIDL(1), client);
	}

	private void setSplitPosition(String pos) {
		if (orientation == ORIENTATION_HORIZONTAL) {
			DOM.setStyleAttribute(splitter, "left", pos);
		} else {
			DOM.setStyleAttribute(splitter, "top", pos);
		}
		iLayout();
	}

	/*
	 * Calculates absolutely positioned container places/sizes (non-Javadoc)
	 * 
	 * @see com.itmill.toolkit.terminal.gwt.client.NeedsLayout#layout()
	 */
	public void iLayout() {
		int wholeSize;
		int pixelPosition;
		ApplicationConnection.getConsole().log("splitterpaneeeli");
		
		switch (orientation) {
		case ORIENTATION_HORIZONTAL:
			wholeSize = DOM.getElementPropertyInt(wrapper, "offsetWidth");
			pixelPosition = DOM.getElementPropertyInt(splitter, "offsetLeft");

			ApplicationConnection.getConsole().log(wholeSize+"");
			ApplicationConnection.getConsole().log(pixelPosition+"");

			DOM
					.setStyleAttribute(firstContainer, "width", pixelPosition
							+ "px");
			int secondContainerWidth = (wholeSize - pixelPosition - SPLITTER_SIZE);
			if (secondContainerWidth < 0)
				secondContainerWidth = 0;
			DOM.setStyleAttribute(secondContainer, "width",
					secondContainerWidth + "px");
			DOM.setStyleAttribute(secondContainer, "left",
					(pixelPosition + SPLITTER_SIZE) + "px");

			break;
		case ORIENTATION_VERTICAL:
			wholeSize = DOM.getElementPropertyInt(wrapper, "clientHeight");
			pixelPosition = DOM.getElementPropertyInt(splitter, "offsetTop");

			ApplicationConnection.getConsole().log(wholeSize+"");
			ApplicationConnection.getConsole().log(pixelPosition+"");

			DOM.setStyleAttribute(firstContainer, "height", pixelPosition
					+ "px");
			int secondContainerHeight = (wholeSize - pixelPosition - SPLITTER_SIZE);
			if (secondContainerHeight < 0)
				secondContainerHeight = 0;
			DOM.setStyleAttribute(secondContainer, "height",
					secondContainerHeight + "px");
			DOM.setStyleAttribute(secondContainer, "top",
					(pixelPosition + SPLITTER_SIZE) + "px");
		default:

			break;
		}

		Util.runAnchestorsLayout(this);
	}

	private void setFirstWidget(Widget w) {
		if (firstChild != null) {
			firstChild.removeFromParent();
		}
		super.add(w, firstContainer);
		firstChild = w;
	}

	private void setSecondWidget(Widget w) {
		if (secondChild != null) {
			secondChild.removeFromParent();
		}
		super.add(w, secondContainer);
		secondChild = w;
	}

	public void setHeight(String height) {
		super.setHeight(height);
		// give sane height
		getOffsetHeight(); // shake IE
		if (getOffsetHeight() < SPLITTER_SIZE)
			super.setHeight((SPLITTER_SIZE * 3) + "px");
	}

	public void setWidth(String width) {
		super.setWidth(width);
		// give sane width
		getOffsetWidth(); // shake IE
		if (getOffsetWidth() < SPLITTER_SIZE)
			super.setWidth((SPLITTER_SIZE * 3) + "px");
	}

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONMOUSEMOVE:
			if (resizing) {
				onMouseMove(event);
			}
			break;
		case Event.ONMOUSEDOWN:
			onMouseDown(event);
			break;
		case Event.ONMOUSEUP:
			if (resizing)
				onMouseUp(event);
			break;
		case Event.ONCLICK:
			resizing = false;
			break;
		}
	}

	public void onMouseDown(Event event) {
		if (DOM.compare(DOM.eventGetTarget(event), splitter)) {
			resizing = true;
			DOM.setCapture(getElement());
			origX = DOM.getElementPropertyInt(splitter, "offsetLeft");
			origY = DOM.getElementPropertyInt(splitter, "offsetTop");
			origMouseX = DOM.eventGetClientX(event);
			origMouseY = DOM.eventGetClientY(event);
			DOM.eventCancelBubble(event, true);
			DOM.eventPreventDefault(event);
		}
	}

	public void onMouseMove(Event event) {
		switch (orientation) {
		case ORIENTATION_HORIZONTAL:
			int x = DOM.eventGetClientX(event);
			onHorizontalMouseMove(x);
			break;
		case ORIENTATION_VERTICAL:
		default:
			int y = DOM.eventGetClientY(event);
			onVerticalMouseMove(y);
			break;
		}
		iLayout();
	}

	private void onHorizontalMouseMove(int x) {
		int newX = origX + x - origMouseX;
		if (newX < 0)
			newX = 0;
		if (newX + SPLITTER_SIZE > getOffsetWidth())
			newX = getOffsetWidth() - SPLITTER_SIZE;
		DOM.setStyleAttribute(splitter, "left", newX + "px");
	}

	private void onVerticalMouseMove(int y) {
		int newY = origY + y - origMouseY;
		if (newY < 0)
			newY = 0;

		if (newY + SPLITTER_SIZE > getOffsetHeight())
			newY = getOffsetHeight() - SPLITTER_SIZE;
		DOM.setStyleAttribute(splitter, "top", newY + "px");
	}

	public void onMouseUp(Event event) {
		DOM.releaseCapture(getElement());
		resizing = false;
		onMouseMove(event);
	}

}
