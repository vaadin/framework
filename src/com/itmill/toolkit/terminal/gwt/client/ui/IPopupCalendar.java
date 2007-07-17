package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IPopupCalendar extends ITextualDate implements Paintable, ClickListener, PopupListener {
	
	private IButton calendarToggle;
	
	private ICalendarPanel calendar;
	
	private PopupPanel popup;
	
	public IPopupCalendar() {
		super();
		
		calendarToggle = new IButton();
		calendarToggle.setText("...");
		calendarToggle.addClickListener(this);
		add(calendarToggle);
		
		calendar = new ICalendarPanel(this);
		popup = new PopupPanel(true);
		popup.setStyleName(IDateField.CLASSNAME+"-calendar");
		popup.setWidget(calendar);
		popup.addPopupListener(this);
	}
	
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		super.updateFromUIDL(uidl, client);
		calendar.updateCalendar();
		calendarToggle.setEnabled(enabled);
	}

	public void onClick(Widget sender) {
		if(sender == calendarToggle) {
				calendar.updateCalendar();
				popup.setPopupPosition(calendarToggle.getAbsoluteLeft(), calendarToggle.getAbsoluteTop() + calendarToggle.getOffsetHeight() + 2);
				popup.show();
				popup.setWidth(calendar.getOffsetWidth() + "px");
				popup.setHeight(calendar.getOffsetHeight() + "px");
		}
	}

	public void onPopupClosed(PopupPanel sender, boolean autoClosed) {
		if(sender == popup)
			buildTime();
	}

}
