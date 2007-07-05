package com.itmill.toolkit.terminal.gwt.client.ui;

import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ICalendar extends IDateField {
	
	private ICalendarPanel body;
	
	private String locale;
	
	public ICalendar() {
		super();
		body = new ICalendarPanel(this);
		container.add(body);
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		super.updateFromUIDL(uidl, client);
		boolean needsRedraw = (locale == null || !locale.equals(currentLocale));
		body.updateCalendar(needsRedraw);
		locale = currentLocale;
	}

}
