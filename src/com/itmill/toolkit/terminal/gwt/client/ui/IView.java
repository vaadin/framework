package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

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
		clear();
		theme = uidl.getStringAttribute("theme");
		com.google.gwt.user.client.Window.setTitle(uidl.getStringAttribute("caption"));
		UIDL childUidl = uidl.getChildUIDL(0);
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
		
		int i=1;
		HashSet removedSubWindows = new HashSet(subWindows);
		while ((childUidl = uidl.getChildUIDL(i++)) != null) {
			if ("window".equals(childUidl.getTag())) {
				Widget w = client.getWidget(childUidl);
				if (subWindows.contains(w)) {
					removedSubWindows.remove(w);
					client.registerPaintable(childUidl.getId(), (Paintable)w);
				} else {
					subWindows.add(w);
					RootPanel.get().add(w);
				}
				((Paintable)w).updateFromUIDL(childUidl, client);
			}
		}
		for (Iterator rem=removedSubWindows.iterator(); rem.hasNext();) {
			IWindow w = (IWindow) rem.next();
			client.unregisterPaintable(w);
			subWindows.remove(w);
			RootPanel.get().remove(w);
		}
	}

}
