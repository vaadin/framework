package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;

public interface Layout extends Paintable {

	void replaceChildComponent(Widget oldComponent, Widget newComponent);

	boolean hasChildComponent(Widget component);

}
