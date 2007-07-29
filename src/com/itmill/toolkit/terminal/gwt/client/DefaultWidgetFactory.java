package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ui.IButton;
import com.itmill.toolkit.terminal.gwt.client.ui.ICalendar;
import com.itmill.toolkit.terminal.gwt.client.ui.ICheckBox;
import com.itmill.toolkit.terminal.gwt.client.ui.IComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.ICustomLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IEmbedded;
import com.itmill.toolkit.terminal.gwt.client.ui.IGridLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IHorizontalLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.ILabel;
import com.itmill.toolkit.terminal.gwt.client.ui.IOptionGroup;
import com.itmill.toolkit.terminal.gwt.client.ui.IPanel;
import com.itmill.toolkit.terminal.gwt.client.ui.IPasswordField;
import com.itmill.toolkit.terminal.gwt.client.ui.IPopupCalendar;
import com.itmill.toolkit.terminal.gwt.client.ui.IScrollTable;
import com.itmill.toolkit.terminal.gwt.client.ui.ISelect;
import com.itmill.toolkit.terminal.gwt.client.ui.ISlider;
import com.itmill.toolkit.terminal.gwt.client.ui.ITablePaging;
import com.itmill.toolkit.terminal.gwt.client.ui.ITabsheet;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextArea;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextField;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextualDate;
import com.itmill.toolkit.terminal.gwt.client.ui.ITree;
import com.itmill.toolkit.terminal.gwt.client.ui.ITwinColSelect;
import com.itmill.toolkit.terminal.gwt.client.ui.IUnknownComponent;
import com.itmill.toolkit.terminal.gwt.client.ui.IVerticalLayout;
import com.itmill.toolkit.terminal.gwt.client.ui.IWindow;

public class DefaultWidgetFactory implements WidgetFactory {

	public Widget createWidget(UIDL uidl) {

		String tag = uidl.getTag();

		if ("button".equals(tag)) {
			if ("switch".equals(uidl.getStringAttribute("type")))
				return new ICheckBox();
			return new IButton();
		}
		else if ("window".equals(tag))
			return new IWindow();
		else if ("orderedlayout".equals(tag)) {
			if ("horizontal".equals(uidl.getStringAttribute("orientation")))
				return new IHorizontalLayout();
			else
				return new IVerticalLayout();
		}
		else if ("label".equals(tag))
			return new ILabel();
		else if ("gridlayout".equals(tag))
			return new IGridLayout();
		else if ("tree".equals(tag))
			return new ITree();
		else if ("select".equals(tag)) {
			if("optiongroup".equals(uidl.getStringAttribute("style")))
				return new IOptionGroup();
			else if("twincol".equals(uidl.getStringAttribute("style")))
				return new ITwinColSelect();
			return new ISelect();
		}
		else if ("panel".equals(tag))
			return new IPanel();
		else if ("component".equals(tag))
			return new IComponent();
		else if ("tabsheet".equals(tag))
			return new ITabsheet();
		else if ("embedded".equals(tag))
			return new IEmbedded();
		else if ("customlayout".equals(tag))
			return new ICustomLayout();
		else if ("textfield".equals(tag)) {
			if(uidl.hasAttribute("multiline"))
				return new ITextArea();
			else if(uidl.getBooleanAttribute("secret"))
				return new IPasswordField();
			return new ITextField();
		}
		else if ("table".equals(tag)) {
			if(uidl.hasAttribute("style")) {
				if("paging".equals(uidl.getStringAttribute("style")))
						return new ITablePaging();
			}
			return new IScrollTable();
		}
		else if("datefield".equals(tag)) {
			if(uidl.hasAttribute("style"))
				if("calendar".equals(uidl.getStringAttribute("style")))
					return new ICalendar();
				else if("text".equals(uidl.getStringAttribute("style")))
					return new ITextualDate();
			return new IPopupCalendar();
		}
		else if("slider".equals(tag))
			return new ISlider();

		return new IUnknownComponent();
	}

	public boolean isCorrectImplementation(Widget currentWidget, UIDL uidl) {

		// TODO This implementation should be optimized
		return GWT.getTypeName(currentWidget).equals(GWT.getTypeName(createWidget(uidl)));
	}

}
