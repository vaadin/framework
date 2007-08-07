package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Caption;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IVerticalLayout extends VerticalPanel implements Paintable, Layout {

	private HashMap componentToCaption = new HashMap();

	private ApplicationConnection client;
	
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		
		this.client = client;
		
		// Ensure correct implementation
		if (client.updateComponent(this, uidl, false))
			return;

		ArrayList uidlWidgets = new ArrayList();
		for (Iterator it = uidl.getChildIterator(); it.hasNext();) {
			UIDL uidlForChild = (UIDL) it.next();
			Widget child = client.getWidget(uidlForChild);
			((Paintable)child).updateFromUIDL(uidlForChild, client);
			uidlWidgets.add(child);
		}
		
		Vector oldWidgets = getPaintables();
		
		Iterator oldIt = oldWidgets.iterator();
		Iterator newIt = uidlWidgets.iterator();
		
		Widget oldChild = null;
		while(newIt.hasNext()) {
			Widget child = (Widget) newIt.next();
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
				continue;
			}
			if(child == oldChild) {
				// child already attached and updated
				if(oldIt.hasNext()) {
					oldChild = (Widget) oldIt.next();
				}
				continue;
			}
			if(hasChildComponent(child)) {
				// current child has been moved
				this.insert(child, getWidgetIndex(oldChild));
			}
		}
		// remove possibly remaining old Paintable object which were not updated 
		while(oldIt.hasNext()) {
			Paintable p = (Paintable) oldIt.next();
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

	public boolean hasChildComponent(Widget component) {
		return getWidgetIndex(component) >= 0;
	}

	public void updateCaption(Widget component, UIDL uidl) {
		
		Caption c  = (Caption) componentToCaption.get(component);
		
		if (Caption.isNeeded(uidl)) {
			if (c == null) {
				int index = getWidgetIndex(component);
				c = new Caption();
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
	
	

}
