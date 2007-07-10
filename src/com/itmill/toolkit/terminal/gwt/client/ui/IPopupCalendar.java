package com.itmill.toolkit.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IPopupCalendar extends IDateField implements Paintable, ChangeListener, ClickListener {
	
	private ITextField text;
	
	private IButton calendarToggle;
	
	private ICalendarPanel calendar;
	
	private PopupPanel popup;
	
	public IPopupCalendar() {
		super();
		text = new ITextField();
		text.addChangeListener(this);
		calendarToggle = new IButton();
		calendarToggle.setText("...");
		calendarToggle.addClickListener(this);
		calendar = new ICalendarPanel(this);
		popup = new PopupPanel(true);
		popup.setStyleName(IDateField.CLASSNAME+"-calendar");
		popup.setWidget(calendar);
		add(text);
		add(calendarToggle);
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		super.updateFromUIDL(uidl, client);
		
		
		
		calendar.updateCalendar();
	}

	public void onChange(Widget sender) {
		
	}

	public void onClick(Widget sender) {
		if(sender == calendarToggle) {
				popup.setPopupPosition(calendarToggle.getAbsoluteLeft(), calendarToggle.getAbsoluteTop() + calendarToggle.getOffsetHeight() + 2);
				popup.show();
				popup.setWidth(calendar.getOffsetWidth() + "px");
				popup.setHeight(calendar.getOffsetHeight() + "px");
		}
	}

}
