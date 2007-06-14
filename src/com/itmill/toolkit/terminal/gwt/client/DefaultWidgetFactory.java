package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.TkPasswordField;
import com.itmill.toolkit.terminal.gwt.client.ui.TkButton;
import com.itmill.toolkit.terminal.gwt.client.ui.TkCheckBox;
import com.itmill.toolkit.terminal.gwt.client.ui.TkEmbedded;
import com.itmill.toolkit.terminal.gwt.client.ui.TkGridLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.TkHorizontalLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.TkLabel;
import com.itmill.toolkit.terminal.gwt.client.ui.TkVerticalLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.TkPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.TkSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.TkTable;
import com.itmill.toolkit.terminal.gwt.client.ui.TkTabsheet;
import com.itmill.toolkit.terminal.gwt.client.ui.TkTextArea;
import com.itmill.toolkit.terminal.gwt.client.ui.TkTextField;
import com.itmill.toolkit.terminal.gwt.client.ui.TkTree;
import com.itmill.toolkit.terminal.gwt.client.ui.TkUnknownComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.TkWindow;

public class DefaultWidgetFactory implements WidgetFactory {

	public Widget createWidget(UIDL uidl) {

		String tag = uidl.getTag();

		if ("button".equals(tag)) {
			if ("switch".equals(uidl.getStringAttribute("type")))
				return new TkCheckBox();
			return new TkButton();
		}
		if ("window".equals(tag))
			return new TkWindow();
		if ("orderedlayout".equals(tag)) {
			if ("horizontal".equals(uidl.getStringAttribute("orientation")))
				return new TkHorizontalLayout();
			else
				return new TkVerticalLayout();
		}
		if ("label".equals(tag))
			return new TkLabel();
		if ("gridlayout".equals(tag))
			return new TkGridLayout();
		if ("tree".equals(tag))
			return new TkTree();
		if ("select".equals(tag))
			return new TkSelect();
		if ("panel".equals(tag) || "component".equals(tag))
			return new TkPanel();
		if ("tabsheet".equals(tag))
			return new TkTabsheet();
		if ("embedded".equals(tag))
			return new TkEmbedded();
		if ("textfield".equals(tag)) {
			if(uidl.hasAttribute("multiline"))
				return new TkTextArea();
			else if(uidl.getBooleanAttribute("secret"))
				return new TkPasswordField();
			return new TkTextField();
		}
		if ("table".equals(tag))
			return new TkTable();

		return new TkUnknownComponent();
	}

	public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl) {

		// TODO This implementation should be optimized
		return GWT.getTypeName(currentWidget).equals(GWT.getTypeName(createWidget(uidl)));
	}

}
