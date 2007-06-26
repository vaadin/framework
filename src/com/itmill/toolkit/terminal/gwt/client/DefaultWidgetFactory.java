package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.IButton;
import com.itmill.toolkit.terminal.gwt.client.ui.ICheckBox;
import com.itmill.toolkit.terminal.gwt.client.ui.IComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.ICustomLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IDateField;
import com.itmill.toolkit.terminal.gwt.client.ui.IEmbedded;
import com.itmill.toolkit.terminal.gwt.client.ui.IGridLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IHorizontalLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.ILabel;
import com.itmill.toolkit.terminal.gwt.client.ui.IOptionGroup;
import com.itmill.toolkit.terminal.gwt.client.ui.IPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.IPasswordField;
import com.itmill.toolkit.terminal.gwt.client.ui.ISelect;
import com.itmill.toolkit.terminal.gwt.client.ui.ITablePaging;
import com.itmill.toolkit.terminal.gwt.client.ui.ITabsheet;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextArea;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextField;
import com.itmill.toolkit.terminal.gwt.client.ui.ITree;
import com.itmill.toolkit.terminal.gwt.client.ui.ITwinColSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IUnknownComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.IVerticalLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IWindow;
import com.itmill.toolkit.terminal.gwt.client.ui.scrolltable.IScrollTable;

public class DefaultWidgetFactory implements WidgetFactory {

	public Widget createWidget(UIDL uidl) {

		String tag = uidl.getTag();

		if ("button".equals(tag)) {
			if ("switch".equals(uidl.getStringAttribute("type")))
				return new ICheckBox();
			return new IButton();
		}
		if ("window".equals(tag))
			return new IWindow();
		if ("orderedlayout".equals(tag)) {
			if ("horizontal".equals(uidl.getStringAttribute("orientation")))
				return new IHorizontalLayout();
			else
				return new IVerticalLayout();
		}
		if ("label".equals(tag))
			return new ILabel();
		if ("gridlayout".equals(tag))
			return new IGridLayout();
		if ("tree".equals(tag))
			return new ITree();
		if ("select".equals(tag)) {
			if("optiongroup".equals(uidl.getStringAttribute("style")))
				return new IOptionGroup();
			else if("twincol".equals(uidl.getStringAttribute("style")))
				return new ITwinColSelect();
			return new ISelect();
		}
		if ("panel".equals(tag))
			return new IPanel();
		if ("component".equals(tag))
			return new IComponent();
		if ("tabsheet".equals(tag))
			return new ITabsheet();
		if ("embedded".equals(tag))
			return new IEmbedded();
		if ("customlayout".equals(tag))
			return new ICustomLayout();
		if ("textfield".equals(tag)) {
			if(uidl.hasAttribute("multiline"))
				return new ITextArea();
			else if(uidl.getBooleanAttribute("secret"))
				return new IPasswordField();
			return new ITextField();
		}
		if ("table".equals(tag)) {
			if(uidl.hasAttribute("style")) {
				if("scrolling".equals(uidl.getStringAttribute("style")))
						return new IScrollTable();
			}
			return new ITablePaging();
		}
		if("datefield".equals(tag))
			return new IDateField();

		return new IUnknownComponent();
	}

	public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl) {

		// TODO This implementation should be optimized
		return GWT.getTypeName(currentWidget).equals(GWT.getTypeName(createWidget(uidl)));
	}

}
