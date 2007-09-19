package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.Container;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

/**
 * Abstract base class for ordered layouts.
 * Use either vertical or horizontal subclass.
 * @author IT Mill Ltd
 */
public abstract class IOrderedLayout extends ComplexPanel implements Container {
	
	public static final String CLASSNAME = "i-orderedlayout";

	public static final int ORIENTATION_VERTICAL = 0;
	public static final int ORIENTATION_HORIZONTAL = 1;
	
	int orientationMode = ORIENTATION_VERTICAL;

	private HashMap componentToCaption = new HashMap();

	private ApplicationConnection client;

	/**
	 * Contains reference to Element where Paintables are wrapped. For  horizontal
	 * layout this is TR and for vertical DIV.
	 */
	private Element childContainer;

	public IOrderedLayout(int orientation) {
		orientationMode = orientation;
		constructDOM();
		setStyleName(CLASSNAME);
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
		
		ArrayList oldWidgets = getPaintables();
		
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
				// TODO this might be optimized by movin only container element
				// to correct position
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
	
	/**
	 * Retuns a list of Paintables currently rendered in layout
	 * 
	 * @return list of Paintable objects
	 */
	private ArrayList getPaintables() {
		ArrayList al = new ArrayList();
		Iterator it = iterator();
		while (it.hasNext()) {
			Widget w = (Widget) it.next();
			if (w instanceof Paintable)
				al.add(w);
		}
		return al;
	}
	
	/**
	 * Removes Paintable from DOM and its reference from ApplicationConnection.
	 * 
	 * Also removes Paintable's Caption if one exists
	 * 
	 * @param p Paintable to be removed
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
		if (w instanceof Caption) {
			Caption c = (Caption) w;
			// captions go into same container element as their
			// owners
			Element container = DOM.getParent(((UIObject) c.getOwner()).getElement());
			Element captionContainer = DOM.createDiv();
			DOM.insertChild(container, captionContainer, 0);
			insert(w, captionContainer, beforeIndex, false);
		} else {
			Element container = createWidgetWrappper();
			DOM.insertChild(childContainer, container, beforeIndex);
			insert(w, container, beforeIndex, false);
		}
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

	public void updateCaption(Paintable component, UIDL uidl) {
		
		Caption c  = (Caption) componentToCaption.get(component);
		
		if (Caption.isNeeded(uidl)) {
			if (c == null) {
				int index = getWidgetIndex((Widget) component);
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
		DOM.appendChild(childContainer, wrapper);
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
				DOM.removeChild(childContainer, wrapper);
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
