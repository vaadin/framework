package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.DateTimeService;

public class ICalendarPanel extends FlexTable implements ClickListener {
	
	private IDateField datefield;
	
	private IButton prevYear;
	private IButton nextYear;
	private IButton prevMonth;
	private IButton nextMonth;
	
	public ICalendarPanel(IDateField parent) {
		datefield = parent;
		// Force table size
		setText(0, 0, "");
		setText(7, 6, "");
		buildCalendar(true);
	}
	
	public void buildCalendar(boolean forceRedraw) {
		buildCalendarHeader(forceRedraw);
		buildCalendarBody();
	}
	
	private void clearCalendarBody() {
		for (int row=2; row < 8; row++){
			for (int col=0; col < 7; col++){
				setText(row, col, "");
			}
		}
	}
	
	private void buildCalendarHeader(boolean forceRedraw) {
		if(forceRedraw) {
			prevYear = new IButton(); prevYear.setText("&laquo;");
			nextYear = new IButton(); nextYear.setText("&raquo;");
			prevMonth = new IButton(); prevMonth.setText("&lsaquo;");
			nextMonth = new IButton(); nextMonth.setText("&rsaquo;");
			prevYear.addClickListener(this); nextYear.addClickListener(this);
			prevMonth.addClickListener(this); nextMonth.addClickListener(this);setWidget(0, 0, prevYear);
			
			setWidget(0, 1, prevMonth);
			setWidget(0, 3, nextMonth);
			setWidget(0, 4, nextYear);
			getFlexCellFormatter().setColSpan(0, 2, 3);
			
			int firstDay = datefield.dts.getFirstDayOfWeek();
			for(int i = 0; i < 7; i++) {
				int day = i + firstDay;
				if(day > 6) day = 0;
				setText(1,i, datefield.dts.getShortDay(day));
			}
		}
		
		String monthName = datefield.dts.getMonth(datefield.date.getMonth());
		int year = datefield.date.getYear()+1900;
		setText(0, 2, monthName + " " + year);
	}
	
	private void buildCalendarBody() {
		Date date = datefield.date;
		int startWeekDay = datefield.dts.getStartWeekDay(date);
		int numDays = DateTimeService.getNumberOfDaysInMonth(date);
		int dayCount = 0;
		for (int row = 2; row < 8; row++){
			for (int col = 0; col < 7; col++){
				if(row == 2 && col < startWeekDay){
					setText(row, col, "");
					//cellValues[row][col] = ""; 
				} else {
					if(numDays > dayCount){
						int selectedDate = ++dayCount;
						//cellValues[row][col] = selectedDate +"";
						//if(true == isSelectedDate(date, selectedDate)){
							//setHTML(row, col, "<font class='currentDate'>" + selectedDate+"<font>");
						//}else{
							setText(row, col, ""+selectedDate);
						//}
					} else {
						setText(row, col, "");
						//cellValues[row][col] = ""; 
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param forceRedraw Build all from scratch, in case of e.g. locale changes
	 */
	public void updateCalendar(boolean forceRedraw) {
		clearCalendarBody();
		buildCalendar(forceRedraw);
	}
	
	public void onClick(Widget sender) {
		if(sender == prevYear) {
			datefield.date.setYear(datefield.date.getYear()-1);
			datefield.client.updateVariable(datefield.id, "year", datefield.date.getYear()+1900, datefield.immediate);
			updateCalendar(false);
		} else if(sender == nextYear) {
			datefield.date.setYear(datefield.date.getYear()+1);
			datefield.client.updateVariable(datefield.id, "year", datefield.date.getYear()+1900, datefield.immediate);
			updateCalendar(false);
		} else if(sender == prevMonth) {
			datefield.date.setMonth(datefield.date.getMonth()-1);
			datefield.client.updateVariable(datefield.id, "month", datefield.date.getMonth()+1, datefield.immediate);
			updateCalendar(false);
		} else if(sender == nextMonth) {
			datefield.date.setMonth(datefield.date.getMonth()+1);
			datefield.client.updateVariable(datefield.id, "month", datefield.date.getMonth()+1, datefield.immediate);
			updateCalendar(false);
		}
	}
}
