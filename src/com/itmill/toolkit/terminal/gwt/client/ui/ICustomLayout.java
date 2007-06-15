package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.CaptionWrapper;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Layout;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICustomLayout extends SimplePanel implements Paintable, Layout {

	private HashMap componentToWrapper = new HashMap();
	HTMLPanel html;
	private static HashMap styleToTemplate = new HashMap();
	String currentStyle;
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		
		if (client.updateComponent(this, uidl, false)) return;
		
		// Initialize HTMLPanel when needed
		String newStyle = uidl.getStringAttribute("style");
		if (currentStyle == null || !currentStyle.equals(newStyle)) {
			String template = (String) styleToTemplate.get(newStyle);
			if (template == null) {
				template = "custom layout of style " + newStyle + " <div location=\"one\"></div>foobar";
				styleToTemplate.put(newStyle, template);
			}
			html = new HTMLPanel(template);
			// TODO Map locations
			add(html);
		}
		
		componentToWrapper.clear();
		
		for (Iterator i = uidl.getChildIterator(); i.hasNext();) {
			UIDL uidlForChild = (UIDL) i.next();
			Widget child = client.getWidget(uidlForChild);
			//html.add(child);
			((Paintable)child).updateFromUIDL(uidlForChild, client);
		}
	}

	public void replaceChildComponent(Widget from, Widget to) {
		CaptionWrapper wrapper = (CaptionWrapper) componentToWrapper.get(from);
		if (wrapper != null) {
			componentToWrapper.remove(from);
			from = wrapper;
		}
		// TODO
		html.remove(from);
		html.add(to);

	}

	public boolean hasChildComponent(Widget component) {
		// TODO
		return componentToWrapper.get(component) != null;
	}

	public void updateCaption(Widget component, UIDL uidl) {
		// TODO
		/*
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
		*/
	}
	
	

}
