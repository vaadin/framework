package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.TkButton;
import com.itmill.toolkit.terminal.gwt.client.ui.TkGridLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.TkLabel;
import com.itmill.toolkit.terminal.gwt.client.ui.TkOrderedLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.TkPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.TkSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.TkTree;
import com.itmill.toolkit.terminal.gwt.client.ui.TkUnknownComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.TkWindow;

public class DefaultWidgetFactory implements WidgetFactory {

	public Widget createWidget(String tag, String theme) {

		if ("button".equals(tag))
			return new TkButton();
		if ("window".equals(tag))
			return new TkWindow();
		if ("orderedlayout".equals(tag))
			return new TkOrderedLayout();
		if ("label".equals(tag))
			return new TkLabel();
		if ("gridlayout".equals(tag))
			return new TkGridLayout();
		if ("tree".equals(tag))
			return new TkTree();
		if ("select".equals(tag))
			return new TkSelect();
		if ("panel".equals(tag))
			return new TkPanel();

		return new TkUnknownComponent();
	}

}
