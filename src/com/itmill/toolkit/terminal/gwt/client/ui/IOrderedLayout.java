package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IOrderedLayout extends ComplexPanel implements Paintable, Layout {
	
	public static final String CLASSNAME = "i-orderedlayout";

	public static final int ORIENTATION_VERTICAL = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;
	
	int orientationMode = ORIENTATION_VERTICAL;

	private HashMap componentToCaption = new HashMap();

	private ApplicationConnection client;
	private Element childContainer;
	
	public IOrderedLayout() {
		orientationMode = ORIENTATION_VERTICAL;
		constructDOM();
		setStyleName(CLASSNAME);
	}

	public IOrderedLayout(int orientation) {
		orientationMode = orientation;
		constructDOM();
	}
	private void constructDOM() {
		switch (orientationMode) {
		case ORIENTATION_HORIZONTAL:
			Element table = DOM.createTable();
			Element tBody = DOM.createTBody();
			childContainer = DOM.createTR();
			DOM.appendChild(table, tBody);
			DOM.appendChild(tBody, childContainer);
			setElement(table);
			break;
		default:
			childContainer = DOM.createDiv();
			setElement(childContainer);
			break;
		}
	}

	
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		
		this.client = client;
		
		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;

		ArrayList uidlWidgets = new ArrayList();
		for (Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL uidlForChild = (UIDL) it.next();
			Widget child = client.getWidget(uidlForChild);
			uidlWidgets.add(child);
		}
		
		Vector oldWidgets = getPaintables();
		
		Iterator oldIt = oldWidgets.iterator();
		Iterator newIt = uidlWidgets.iterator();
		Iterator newUidl = uidl.getChildIterator();
		
		Widget oldChild = null;
		while(newIt.hasNext()) {
			Widget child = (Widget) newIt.next();
			UIDL childUidl = (UIDL) newUidl.next();
			if(oldChild == null && oldIt.hasNext()) {
				// search for next old Paintable which still exists in layout
				// and delete others
				while(oldIt.hasNext()) {
					oldChild = (Widget) oldIt.next();
					// now oldChild is an instance of Paintable
					if(uidlWidgets.contains(oldChild))
						break;
					else {
						removePaintable((Paintable) oldChild);
						oldChild = null;
					}
				}
			}
			if(oldChild == null) {
				// we are adding components to layout
				add(child);
			} else if(child == oldChild) {
				// child already attached and updated
				oldChild = null;
			} else if(hasChildComponent(child)) {
				// current child has been moved, re-insert before current oldChild
				removeCaption(child);
				int index = getWidgetIndex(oldChild);
				if(componentToCaption.containsKey(oldChild))
					index--;
				remove(child);
				this.insert(child, index);
			}
			((Paintable)child).updateFromUIDL(childUidl, client);

		}
		// remove possibly remaining old Paintable object which were not updated 
		while(oldIt.hasNext()) {
			oldChild = (Widget) oldIt.next();
			Paintable p = (Paintable) oldChild;
			if(!uidlWidgets.contains(p))
				removePaintable(p);
		}
	}
	
	private Vector getPaintables() {
		Vector al = new Vector();
		Iterator it = iterator();
		while (it.hasNext()) {
			Widget w = (Widget) it.next();
			if (w instanceof Paintable)
				al.add(w);
		}
		return al;
	}
	
	/**
	 * Removes Paintable from DOM and its reference from ApplicationConnection
	 */
	public boolean removePaintable(Paintable p) {
		Caption c = (Caption) componentToCaption.get(p);
		if(c != null) {
			componentToCaption.remove(c);
			remove(c);
		}
		client.unregisterPaintable(p);
		return remove((Widget) p);
	}

	/* (non-Javadoc)
	 * @see com.itmill.toolkit.terminal.gwt.client.Layout#replaceChildComponent(com.google.gwt.user.client.ui.Widget, com.google.gwt.user.client.ui.Widget)
	 */
	public void replaceChildComponent(Widget from, Widget to) {
		client.unregisterPaintable((Paintable) from);
		Caption c = (Caption) componentToCaption.get(from);
		if (c != null) {
			remove(c);
			componentToCaption.remove(c);
		}
		int index = getWidgetIndex(from);
		if (index >= 0) {
			remove(index);
			insert(to, index);
		}
	}

	private void insert(Widget w, int beforeIndex) {
		Element container;
		if (w instanceof Caption) {
			// captions go into same container element as their
			// owners
			container = DOM.getParent(getWidget(beforeIndex).getElement());
			DOM.insertChild(container, w.getElement(), 0);
			insert(w, null, beforeIndex);
		} else {
			container = createWidgetWrappper();
			DOM.insertChild(getChildContainer(), container, beforeIndex);
			insert(w, container, beforeIndex);
		}
	}
	
	/**
	 * @return Element 
	 * 				where widgets (and their wrappers) are contained 
	 */
	private Element getChildContainer() {
		return childContainer;
	}
	
	/**
	 * creates an Element which will contain child widget
	 */
	private Element createWidgetWrappper() {
		switch (orientationMode) {
		case ORIENTATION_HORIZONTAL:
			return DOM.createTD();
		default:
			return DOM.createDiv();
		}
	}

	public boolean hasChildComponent(Widget component) {
		return getWidgetIndex(component) >= 0;
	}

	public void updateCaption(Widget component, UIDL uidl) {
		
		Caption c  = (Caption) componentToCaption.get(component);
		
		if (Caption.isNeeded(uidl)) {
			if (c == null) {
				int index = getWidgetIndex(component);
				c = new Caption(component);
				insert(c, index);
				componentToCaption.put(component, c);
			}
			c.updateCaption(uidl);
		} else {
			if (c != null) { 
				remove(c);
				componentToCaption.remove(component);
			}
		}
	}
	
	public void removeCaption(Widget w) {
		Caption c = (Caption) componentToCaption.get(w);
		if(c != null) {
			this.remove(c);
			componentToCaption.remove(w);
		}
	}

	public void add(Widget w) {
		Element wrapper = createWidgetWrappper();
		DOM.appendChild(getChildContainer(), wrapper);
		super.add(w,wrapper);
	}

	public boolean remove(int index) {
	    return remove(getWidget(index));
	}
	
	public boolean remove(Widget w) {
		Element wrapper = DOM.getParent(w.getElement());
		boolean removed = super.remove(w);
		if(removed) {
			if (! (w instanceof Caption)) {
				DOM.removeChild(getChildContainer(), wrapper);
			}
			return true;
		}
		return false;
	}

	public Widget getWidget(int index) {
		return getChildren().get(index);
	}

	public int getWidgetCount() {
		return getChildren().size();
	}

	public int getWidgetIndex(Widget child) {
		return getChildren().indexOf(child);
	}

}
