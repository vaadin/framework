package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.CaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class TkVerticalLayout extends VerticalPanel implements Paintable, Layout {

	private HashMap componentToWrapper = new HashMap();
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		
		// Ensure correct implementation
		if (client.replaceComponentWithCorrectImplementation(this, uidl))
			return;

		// TODO Should update instead of just redraw
		clear();
		
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL) i.next();
			Widget child = client.getWidget(uidlForChild);
			add(child);
			((Paintable)child).updateFromUIDL(uidlForChild, client);
		}
	}

	public void replaceChildComponent(Widget from, Widget to) {
		CaptionWrapper wrapper = (CaptionWrapper) componentToWrapper.get(from);
		if (wrapper != null) {
			componentToWrapper.remove(from);
			from = wrapper;
		}
		int index = getWidgetIndex(from);
		if (index >= 0) {
			remove(index);
			insert(to, index);
		}
	}

	public boolean hasChildComponent(Widget component) {
		return getWidgetIndex(component) >= 0 || componentToWrapper.get(component) != null;
	}

	public void updateCaption(Widget component, UIDL uidl) {
		
		CaptionWrapper wrapper = (CaptionWrapper) componentToWrapper.get(component);
		if (CaptionWrapper.isNeeded(uidl)) {
			if (wrapper == null) {
				int index = getWidgetIndex(component);
				remove(component);
				wrapper = new CaptionWrapper(component);
				insert(wrapper, index);
				componentToWrapper.put(component, wrapper);
			}
			wrapper.updateCaption(uidl);
		} else {
			if (wrapper != null) { 
				int index = getWidgetIndex(wrapper);
				remove(wrapper);
				insert(wrapper.getWidget(), index);
				componentToWrapper.remove(component);
			}
		}
	}
	
	

}
