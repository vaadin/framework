package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.ContainerResizedListener;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.Util;

/**
 * TODO make this work horizontally
 * 
 * @author IT Mill Ltd
 */
public class IExpandLayout extends IOrderedLayout implements
		ContainerResizedListener {
	public static final String CLASSNAME = "i-expandlayout";

	private Widget expandedWidget;
	private UIDL expandedWidgetUidl;

	public IExpandLayout() {
		super(IOrderedLayout.ORIENTATION_VERTICAL);
		setStyleName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		this.client = client;

		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;

		String h = uidl.getStringAttribute("height");
		setHeight(h);
		String w = uidl.getStringAttribute("width");
		setWidth(w);

		ArrayList uidlWidgets = new ArrayList();
		for (Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL cellUidl = (UIDL) it.next();
			Widget child = client.getWidget(cellUidl.getChildUIDL(0));
			uidlWidgets.add(child);
			if (cellUidl.hasAttribute("expanded")) {
				expandedWidget = child;
				expandedWidgetUidl = cellUidl.getChildUIDL(0);
			}
		}

		ArrayList oldWidgets = getPaintables();

		Iterator oldIt = oldWidgets.iterator();
		Iterator newIt = uidlWidgets.iterator();
		Iterator newUidl = uidl.getChildIterator();

		Widget oldChild = null;
		while (newIt.hasNext()) {
			Widget child = (Widget) newIt.next();
			UIDL childUidl = ((UIDL) newUidl.next()).getChildUIDL(0);
			if (oldChild == null && oldIt.hasNext()) {
				// search for next old Paintable which still exists in layout
				// and delete others
				while (oldIt.hasNext()) {
					oldChild = (Widget) oldIt.next();
					// now oldChild is an instance of Paintable
					if (uidlWidgets.contains(oldChild))
						break;
					else {
						removePaintable((Paintable) oldChild);
						oldChild = null;
					}
				}
			}
			if (oldChild == null) {
				// we are adding components to layout
				add(child);
			} else if (child == oldChild) {
				// child already attached and updated
				oldChild = null;
			} else if (hasChildComponent(child)) {
				// current child has been moved, re-insert before current
				// oldChild
				// TODO this might be optimized by moving only container element
				// to correct position
				removeCaption(child);
				int index = getWidgetIndex(oldChild);
				if (componentToCaption.containsKey(oldChild))
					index--;
				remove(child);
				this.insert(child, index);
			} else {
				// insert new child before old one
				int index = getWidgetIndex(oldChild);
				insert(child, index);
			}
			if (child != expandedWidget)
				((Paintable) child).updateFromUIDL(childUidl, client);
		}
		// remove possibly remaining old Paintable object which were not updated
		while (oldIt.hasNext()) {
			oldChild = (Widget) oldIt.next();
			Paintable p = (Paintable) oldChild;
			if (!uidlWidgets.contains(p))
				removePaintable(p);
		}

		if (uidlWidgets.size() == 0)
			return;

		iLayout();

		/*
		 * Expanded widget is updated after layout function so it has its
		 * container fixed at the moment of updateFromUIDL.
		 */
		((Paintable) expandedWidget).updateFromUIDL(expandedWidgetUidl, client);

	}

	public void iLayout() {
		if (expandedWidget == null) {
			return;
		}
		// ApplicationConnection.getConsole().log("EL layouting...");
		Element expandedElement = DOM.getParent(expandedWidget.getElement());
		// take expanded element temporarely out of flow to make container
		// minimum sized
		String origiginalPositioning = DOM.getStyleAttribute(expandedWidget.getElement(), "position");
		DOM.setStyleAttribute(expandedWidget.getElement(), "position", "absolute");
		DOM.setStyleAttribute(expandedElement, "height", "");

		// add temp element to make some measurements
		Element meter = createWidgetWrappper();
		DOM.setStyleAttribute(meter, "overflow", "hidden");
		DOM.setStyleAttribute(meter, "height", "0");
		DOM.appendChild(childContainer, meter);
		int usedSpace = DOM.getElementPropertyInt(meter, "offsetTop")
				- DOM.getElementPropertyInt(DOM.getFirstChild(childContainer),
						"offsetTop");
		// ApplicationConnection.getConsole().log("EL h" + getOffsetHeight());
		// ApplicationConnection.getConsole().log("EL h" + getOffsetHeight());
		int freeSpace = getOffsetHeight() - usedSpace;

		DOM.setStyleAttribute(expandedElement, "height", freeSpace + "px");
		// Component margins will bleed if overflow is not hidden
		DOM.setStyleAttribute(expandedElement, "overflow", "hidden");

		DOM.setStyleAttribute(expandedWidget.getElement(), "position", origiginalPositioning);

		DOM.removeChild(childContainer, meter);

		// TODO save previous size and only propagate if really changed
		Util.runAnchestorsLayout(this);
	}

}
