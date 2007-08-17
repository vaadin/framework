package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.ApplicationConnection;
import com.itmill.toolkit.terminal.gwt.client.UIDL;
import com.itmill.toolkit.terminal.gwt.client.ui.datefield.ICalendarPanel;

public class IDateFieldCalendar extends IDateField {
	
	private ICalendarPanel date;
	
	public IDateFieldCalendar() {
		super();
		setStyleName(CLASSNAME+"-calendar");
		date = new ICalendarPanel(this);
		add(date);
	}
	
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		super.updateFromUIDL(uidl, client);
		date.updateCalendar();
	}

}
