package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICustomComponent extends SimplePanel implements Paintable {
	
	private static final String CLASSNAME = "i-customcomponent";

	public ICustomComponent() {
		super();
		setStyleName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		
		UIDL child = uidl.getChildUIDL(0);
		if(child != null) {
			Paintable p = (Paintable) client.getWidget(child);
			if(p != getWidget()) {
				if(getWidget() != null) {
					client.unregisterPaintable((Paintable) getWidget());
					clear();
				}
				setWidget((Widget) p);
			}
			p.updateFromUIDL(child, client);
		}

	}

}
