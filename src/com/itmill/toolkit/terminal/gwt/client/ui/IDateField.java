package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class IDateField extends Composite implements Paintable, ChangeListener {
	
	
	/*
	 * This implementation is old already.
	 * We should use the DateTimeService class (which is a draft) to get 
	 * locale specific strings and use them to build the datefield 
	 * and its different styles (with inheritance of course).
	 * 
	 * Drafting usage patterns:
	 * 
	 * DateTimeService dts = new DateTimeService(client, "fi_FI");
	 * String month = dts.getMonth(0); // Returns "January"
	 * String day = dts.getDay(19); // Returns e.g. "sunday"
	 * String dateformat = dts.getDateFormat(); // Returns something like MM_/_DD_/_YYYY
	 * String timeformat = dts.getTimeFormat(); // Returns something like HH_:_MM
	 * String date = dts.parseDate(new Date()); // Returns e.g. 6/19/2007 14:32
	 * String date = dts.parseFullDate(new Date()); // Returns e.g. Tuesday 6th June 2007 14:32
	 */
	
	
	public static final String CLASSNAME = "i-datefield";
	
	String id;
	
	Client client;
	
	private boolean immediate;
	
	private FlowPanel container;
	
	private ListBox year;
	
	private static int RESOLUTION_YEAR = 0;
	private static int RESOLUTION_MONTH = 1;
	private static int RESOLUTION_DAY = 2;
	private static int RESOLUTION_HOUR = 3;
	private static int RESOLUTION_MIN = 4;
	private static int RESOLUTION_SEC = 5;
	private static int RESOLUTION_MSEC = 6;

	private int currentResolution = RESOLUTION_YEAR;
	
	public IDateField() {
		container = new FlowPanel();
		initWidget(container);
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		// Ensure correct implementation and let layout manage caption
		if (client.updateComponent(this, uidl, true))
			return;

		// Save details
		this.client = client;
		id = uidl.getId();
		immediate = uidl.getBooleanAttribute("immediate");
		
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
		
		if(uidl.hasVariable("year")) {
			int selectedYear = uidl.getIntVariable("year");
			int y = container.getWidgetIndex(year);
			if(y > -1) {
				year = (ListBox) container.getWidget(y);
				// Deselect old value
				year.setItemSelected(year.getSelectedIndex(), false);
				// and select new
				for(int i=0; i < year.getItemCount(); i++)
					if(year.getValue(i).equals(""+selectedYear)) {
						year.setSelectedIndex(i);
						break;
					}
			} else {
				year = new ListBox();
				year.setStyleName(ISelect.CLASSNAME);
				int today = 1900 + (new Date()).getYear();
				for(int i=1970; i<today+400; i++) {
					year.addItem(""+i, ""+i);
					if(i == selectedYear)
						year.setSelectedIndex(year.getItemCount()-1);
				}
				year.addChangeListener(this);
				container.add(year);
			}
		}
		
		
		currentResolution = newResolution;
	}

	public void onChange(Widget sender) {
		if(sender == year && client != null)
			client.updateVariable(id, "year", year.getValue(year.getSelectedIndex()), immediate);
		
	}

}
