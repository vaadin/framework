package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.Client;
import com.itmill.toolkit.terminal.gwt.client.Paintable;
import com.itmill.toolkit.terminal.gwt.client.UIDL;

public class ITextualDate extends IDateField implements Paintable, ChangeListener {
	
	private ListBox year;
	
	private ListBox month;
	
	private ListBox day;
	
	public ITextualDate() {
		
	}
	
	public void updateFromUIDL(UIDL uidl, Client client) {
		super.updateFromUIDL(uidl, client);
		
		if(uidl.hasVariable("year")) {
			int selectedYear = uidl.getIntVariable("year");
			int y = getWidgetIndex(year);
			if(y > -1 || !currentLocale.equals(uidl.getStringAttribute("locale"))) {
				year = (ListBox) getWidget(y);
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
				add(year);
			}
		}
		if(uidl.hasVariable("month")) {
			int selectedMonth = uidl.getIntVariable("month");
			int y = getWidgetIndex(month);
			if(y > -1) {
				month = (ListBox) getWidget(y);
				// Deselect old value
				month.setItemSelected(month.getSelectedIndex(), false);
				// and select new
				for(int i=0; i < month.getItemCount(); i++)
					if(month.getValue(i).equals(""+selectedMonth)) {
						month.setSelectedIndex(i);
						break;
					}
			} else {
				month = new ListBox();
				month.setStyleName(ISelect.CLASSNAME);
				int today = (new Date()).getMonth();
				for(int i=0; i<12; i++) {
					month.addItem(""+(i+1), ""+i);
					if(i == selectedMonth)
						month.setSelectedIndex(month.getItemCount()-1);
				}
				month.addChangeListener(this);
				add(month);
			}
		}
		if(uidl.hasVariable("day")) {
			int selectedMonth = uidl.getIntVariable("day");
			int y = getWidgetIndex(day);
			if(y > -1) {
				day = (ListBox) getWidget(y);
				// Deselect old value
				day.setItemSelected(day.getSelectedIndex(), false);
				// and select new
				for(int i=0; i <day.getItemCount(); i++)
					if(day.getValue(i).equals(""+selectedMonth)) {
						day.setSelectedIndex(i);
						break;
					}
			} else {
				day = new ListBox();
				day.setStyleName(ISelect.CLASSNAME);
				int today = (new Date()).getDay();
				for(int i=0; i<31; i++) {
					day.addItem(""+(i+1), ""+i);
					if(i == selectedMonth)
						day.setSelectedIndex(day.getItemCount()-1);
				}
				day.addChangeListener(this);
				add(day);
			}
		}
	}

	public void onChange(Widget sender) {
		if(sender == year && client != null)
			client.updateVariable(id, "year", year.getValue(year.getSelectedIndex()), immediate);
		
	}

}
