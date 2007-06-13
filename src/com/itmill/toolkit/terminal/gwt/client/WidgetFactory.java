package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;

public interface WidgetFactory {

	Widget createWidget(UIDL uidl, String theme);
	
}
