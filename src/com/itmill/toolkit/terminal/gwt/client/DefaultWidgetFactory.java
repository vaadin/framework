package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.Button;
import com.itmill.toolkit.terminal.gwt.client.ui.GridLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.Label;
import com.itmill.toolkit.terminal.gwt.client.ui.OrderedLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.Window;

public class DefaultWidgetFactory implements WidgetFactory {

	public Widget createWidget(String tag, String theme) {

		if ("button".equals(tag))
			return new Button();
		if ("window".equals(tag))
			return new Window();
		if ("orderedlayout".equals(tag))
			return new OrderedLayout();
		if ("label".equals(tag))
			return new Label();
		if ("gridlayout".equals(tag))
			return new GridLayout();

		return null;
	}

}
