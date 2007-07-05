package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.DateTimeService;
import com.itmill.toolkit.terminal.gwt.client.LocaleNotLoadedException;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IDateField extends Composite implements Paintable {

	public static final String CLASSNAME = "i-datefield";
	
	String id;
	
	Client client;

	protected FlowPanel container;
	
	protected boolean immediate;
	
	protected static int RESOLUTION_YEAR = 0;
	protected static int RESOLUTION_MONTH = 1;
	protected static int RESOLUTION_DAY = 2;
	protected static int RESOLUTION_HOUR = 3;
	protected static int RESOLUTION_MIN = 4;
	protected static int RESOLUTION_SEC = 5;
	protected static int RESOLUTION_MSEC = 6;
	protected int currentResolution = RESOLUTION_YEAR;
	
	protected String currentLocale;
	
	protected Date date;
	
	protected DateTimeService dts;
	
	public IDateField() {
		container = new FlowPanel();
		initWidget(container);
		date = new Date();
		dts = new DateTimeService();
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		// Ensure correct implementation and let layout manage caption
		if (client.updateComponent(this, uidl, true))
			return;

		// Save details
		this.client = client;
		id = uidl.getId();
		immediate = uidl.getBooleanAttribute("immediate");
		
		if(uidl.hasAttribute("locale")) {
			String locale = uidl.getStringAttribute("locale");
			try {
				dts.setLocale(locale);
				currentLocale = locale;
			} catch (LocaleNotLoadedException e) {
				dts = new DateTimeService();
				currentLocale = dts.getLocale();
				System.out.println("Tried to use an unloaded locale \"" + locale + "\". Using default locale (" + currentLocale + ").");
			}
		}
		
		int newResolution = RESOLUTION_YEAR;
		if(uidl.hasAttribute("month"))
			newResolution = RESOLUTION_MONTH;
		if(uidl.hasAttribute("day"))
			newResolution = RESOLUTION_DAY;
		if(uidl.hasAttribute("hour"))
			newResolution = RESOLUTION_HOUR;
		if(uidl.hasAttribute("min"))
			newResolution = RESOLUTION_MIN;
		if(uidl.hasAttribute("sec"))
			newResolution = RESOLUTION_SEC;
		if(uidl.hasAttribute("msec"))
			newResolution = RESOLUTION_MSEC;
		
		if(currentResolution > newResolution)
			container.clear();
		
		currentResolution = newResolution;
		
		int year = uidl.getIntAttribute("year");
		int month = uidl.getIntAttribute("month");
		int day = uidl.getIntAttribute("day");
		int hour = uidl.getIntAttribute("hour");
		int min = uidl.getIntAttribute("min");
		int sec = uidl.getIntAttribute("sec");
		int msec = uidl.getIntAttribute("msec");
		
		date = new Date((long) buildDate(year, month, day, hour, min, sec, msec));
		
	}
	
	/*
	 * We need this redundant native function because 
	 * GWT hasn't implemented setMilliseconds to the Date class.
	 */
	private native double buildDate(int y, int m, int d, int h, int mi, int s, int ms) /*-{
	try {
		var date = new Date();
		if(y) date.setFullYear(y-1900);
		if(m) date.setMonth(m-1);
		if(d) date.setDate(d);
		if(h) date.setHour(h);
		if(mi) date.setMinutes(mi);
		if(s) date.setSeconds(s);
		if(ms) date.setMilliseconds(ms);
		return date.getTime();
	} catch (e) {
		console.error(e);
	}
	}-*/;

}
