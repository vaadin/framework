package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IView extends SimplePanel implements Paintable {
	
	private String theme;
	
	private Paintable layout;
	
	private HashSet subWindows = new HashSet();


	
	public String getTheme() {
		return theme;
	}
	
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		
		// Start drawing from scratch
		clear();
		
		// Some attributes to note
		theme = uidl.getStringAttribute("theme");
		com.google.gwt.user.client.Window.setTitle(uidl.getStringAttribute("caption"));
		
		// Process children
		int childIndex = 0;
		
		// Open URL:s
		while (childIndex < uidl.getChidlCount() && 
				"open".equals(uidl.getChildUIDL(childIndex).getTag())) {
			UIDL open = uidl.getChildUIDL(childIndex);
			String url = open.getStringAttribute("src");
			String target = open.getStringAttribute("target");
			Window.open(url, target != null ? target : null, "");
			childIndex++;
		}
		
		// Draw this application level window
		UIDL childUidl = uidl.getChildUIDL(childIndex);
		Paintable lo = (Paintable) client.getWidget(childUidl);
		if(layout != null) {
			if(layout != lo) {
				// remove old
				client.unregisterPaintable(layout);
				// add new
				setWidget((Widget) lo);
				layout = lo;
			}
		} else {
			setWidget((Widget) lo);
		}
		lo.updateFromUIDL(childUidl, client);
		
		// Update subwindows
		HashSet removedSubWindows = new HashSet(subWindows);
		
		// Open new windows
		while ((childUidl = uidl.getChildUIDL(childIndex++)) != null) {
			if ("window".equals(childUidl.getTag())) {
				Widget w = client.getWidget(childUidl);
				if (subWindows.contains(w)) {
					removedSubWindows.remove(w);
				} else {
					subWindows.add(w);
				}
				((Paintable)w).updateFromUIDL(childUidl, client);
			}
		}
		
		// Close old windows
		for (Iterator rem=removedSubWindows.iterator(); rem.hasNext();) {
			IWindow w = (IWindow) rem.next();
			client.unregisterPaintable(w);
			subWindows.remove(w);
			RootPanel.get().remove(w);
		}
	}

}
