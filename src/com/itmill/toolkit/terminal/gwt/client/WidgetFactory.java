package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;

public interface WidgetFactory {

	Widget createWidget(UIDL uidl);

	boolean isCorrectImplementation(Widget currentWidget, UIDL uidl);
	
}
