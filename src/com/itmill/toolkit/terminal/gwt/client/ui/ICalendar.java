package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICalendar extends IDateField {
	
	private ICalendarPanel date;
	
	public ICalendar() {
		super();
		setStyleName(CLASSNAME+"-calendar");
		date = new ICalendarPanel(this);
		add(date);
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		super.updateFromUIDL(uidl, client);
		date.updateCalendar();
	}

}
