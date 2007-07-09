package com.itmill.toolkit.terminal.gwt.client.ui;

import java.util.Date;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;
import com.itmill.toolkit.terminal.gwt.client.DateTimeService;
import com.itmill.toolkit.terminal.gwt.client.LocaleService;

public class ICalendarPanel extends FlexTable implements MouseListener, ClickListener {
	
	private IDateField datefield;
	
	private IEventButton prevYear;
	private IEventButton nextYear;
	private IEventButton prevMonth;
	private IEventButton nextMonth;
	
	private ITime time;
	
	/* Needed to identify resolution changes */
	private int resolution = IDateField.RESOLUTION_YEAR;
	
	/* Needed to identify locale changes */
	private String locale = LocaleService.getDefaultLocale();
	
	public ICalendarPanel(IDateField parent) {
		datefield = parent;
		setStyleName(datefield.CLASSNAME+"-calendarpanel");
		//buildCalendar(true);
		addTableListener(new DateClickListener(this));
	}
	
	private void buildCalendar(boolean forceRedraw) {
		boolean needsMonth = datefield.currentResolution > IDateField.RESOLUTION_YEAR;
		boolean needsBody = datefield.currentResolution >= IDateField.RESOLUTION_DAY;
		boolean needsTime = datefield.currentResolution >= IDateField.RESOLUTION_HOUR;
		buildCalendarHeader(forceRedraw, needsMonth);
		clearCalendarBody(!needsBody);
		if(needsBody)
			buildCalendarBody();
		if(needsTime)
			buildTime(forceRedraw);
		else if(time != null) {
			remove(time);
			time = null;
		}
	}

	private void clearCalendarBody(boolean remove) {
		if(!remove) {
			for (int row = 2; row < 8; row++) {
				for (int col = 0; col < 7; col++) {
					setHTML(row, col, "&nbsp;");
				}
			}
		} else if(getRowCount() > 2) {
			while(getRowCount() > 2)
				removeRow(2);
		}
	}
	
	private void buildCalendarHeader(boolean forceRedraw, boolean needsMonth) {
		if(forceRedraw) {
			if(prevMonth == null) { // Only do once
				prevYear = new IEventButton(); prevYear.setHTML("&laquo;");
				nextYear = new IEventButton(); nextYear.setHTML("&raquo;");
				prevYear.addMouseListener(this); nextYear.addMouseListener(this);
				prevYear.addClickListener(this); nextYear.addClickListener(this);
				setWidget(0, 0, prevYear);
				setWidget(0, 4, nextYear);
				
				if(needsMonth) {
					prevMonth = new IEventButton(); prevMonth.setHTML("&lsaquo;");
					nextMonth = new IEventButton(); nextMonth.setHTML("&rsaquo;");
					prevMonth.addMouseListener(this); nextMonth.addMouseListener(this);
					prevMonth.addClickListener(this); nextMonth.addClickListener(this);
					setWidget(0, 3, nextMonth);
					setWidget(0, 1, prevMonth);
				}
				
				getFlexCellFormatter().setColSpan(0, 2, 3);
			} else if(!needsMonth){
				// Remove month traverse buttons
				prevMonth.removeClickListener(this);
				prevMonth.removeMouseListener(this);
				nextMonth.removeClickListener(this);
				nextMonth.removeMouseListener(this);
				remove(prevMonth);
				remove(nextMonth);
				prevMonth = null; nextMonth = null;
			}
			
			// Print weekday names
			int firstDay = datefield.dts.getFirstDayOfWeek();
			for(int i = 0; i < 7; i++) {
				int day = i + firstDay;
				if(day > 6) day = 0;
				if(datefield.currentResolution > IDateField.RESOLUTION_MONTH)
					setHTML(1,i, "<strong>" + datefield.dts.getShortDay(day) + "</strong>");
				else
					setHTML(1,i, "");
			}
		}
		
		String monthName = needsMonth? datefield.dts.getMonth(datefield.date.getMonth()) : "";
		int year = datefield.date.getYear()+1900;
		setHTML(0, 2, "<span class=\""+datefield.CLASSNAME+"-calendarpanel-month\">" + monthName + " " + year + "</span>");
	}
	
	private void buildCalendarBody() {
		Date date = datefield.date;
		int startWeekDay = datefield.dts.getStartWeekDay(date);
		int numDays = DateTimeService.getNumberOfDaysInMonth(date);
		int dayCount = 0;
		Date today = new Date();
		for (int row = 2; row < 8; row++){
			for (int col = 0; col < 7; col++){
				if(!(row == 2 && col < startWeekDay)) {
					if(dayCount < numDays){
						int selectedDate = ++dayCount;
						if(date.getDate() == dayCount){
							setHTML(row, col, "<span class=\""+datefield.CLASSNAME+"-calendarpanel-day-selected\">" + selectedDate + "</span>");
						} else if(today.getDate() == dayCount && today.getMonth() == date.getMonth() && today.getYear() == date.getYear()){
							setHTML(row, col, "<span class=\""+datefield.CLASSNAME+"-calendarpanel-day-today\">" + selectedDate + "</span>");
						} else {
							setHTML(row, col, "<span class=\""+datefield.CLASSNAME+"-calendarpanel-day\">" + selectedDate + "</span>");
						}
					} else {
						break;
					}
				}
			}
		}
	}
	
	private void buildTime(boolean forceRedraw) {
		if(time == null) {
			time = new ITime(datefield);
			setText(8,0,""); // Add new row
			getFlexCellFormatter().setColSpan(8, 0, 7);
			setWidget(8, 0, time);
		}
		time.updateTime(forceRedraw);
	}
	
	/**
	 * 
	 * @param forceRedraw Build all from scratch, in case of e.g. locale changes
	 */
	public void updateCalendar() {
		// Locale and resolution changes force a complete redraw
		buildCalendar(locale != datefield.currentLocale || resolution != datefield.currentResolution);
		locale = datefield.currentLocale;
		resolution = datefield.currentResolution;
	}
	
	public void onClick(Widget sender) {
		processClickEvent(sender);
	}

	private void processClickEvent(Widget sender) {
		if(!datefield.enabled || datefield.readonly)
			return;
		if(sender == prevYear) {
			datefield.date.setYear(datefield.date.getYear()-1);
			datefield.client.updateVariable(datefield.id, "year", datefield.date.getYear()+1900, datefield.immediate);
			updateCalendar();
		} else if(sender == nextYear) {
			datefield.date.setYear(datefield.date.getYear()+1);
			datefield.client.updateVariable(datefield.id, "year", datefield.date.getYear()+1900, datefield.immediate);
			updateCalendar();
		} else if(sender == prevMonth) {
			datefield.date.setMonth(datefield.date.getMonth()-1);
			datefield.client.updateVariable(datefield.id, "month", datefield.date.getMonth()+1, datefield.immediate);
			updateCalendar();
		} else if(sender == nextMonth) {
			datefield.date.setMonth(datefield.date.getMonth()+1);
			datefield.client.updateVariable(datefield.id, "month", datefield.date.getMonth()+1, datefield.immediate);
			updateCalendar();
		}
	}
	
	private Timer timer;

	public void onMouseDown(final Widget sender, int x, int y) {
		if(sender instanceof IEventButton) {
			timer = new Timer() {
				public void run() {
					processClickEvent(sender);
				}
			};
			timer.scheduleRepeating(100);
		}
	}

	public void onMouseEnter(Widget sender) {}

	public void onMouseLeave(Widget sender) {
		if(timer != null)
			timer.cancel();
	}

	public void onMouseMove(Widget sender, int x, int y) {}

	public void onMouseUp(Widget sender, int x, int y) {
		if(timer != null)
			timer.cancel();
	}
	
	private class IEventButton extends IButton implements SourcesMouseEvents {

		private MouseListenerCollection mouseListeners;
		
		public IEventButton() {
			super();
			sinkEvents(Event.FOCUSEVENTS | Event.KEYEVENTS | Event.ONCLICK
				      | Event.MOUSEEVENTS);
		}
		
		public void addMouseListener(MouseListener listener) {
		    if (mouseListeners == null) {
		    	mouseListeners = new MouseListenerCollection();
		    }
		    mouseListeners.add(listener);
		}

		public void removeMouseListener(MouseListener listener) {
			if (mouseListeners != null)
				mouseListeners.remove(listener);
		}
			
		public void onBrowserEvent(Event event) {
			super.onBrowserEvent(event);
			switch (DOM.eventGetType(event)) {
				case Event.ONMOUSEDOWN:
				case Event.ONMOUSEUP:
				case Event.ONMOUSEMOVE:
				case Event.ONMOUSEOVER:
				case Event.ONMOUSEOUT:
					if (mouseListeners != null) {
						mouseListeners.fireMouseEvent(this, event);
					}
					break;
			}
		}	
	}
	
	private class DateClickListener implements TableListener {
		
		private ICalendarPanel cal;
		
		public DateClickListener(ICalendarPanel panel) {
			cal = panel;
		}

		public void onCellClicked(SourcesTableEvents sender, int row, int col) {
			if(sender != cal || row < 2 || row > 7 || !cal.datefield.enabled || cal.datefield.readonly)
				return;
			
			Integer day = new Integer(cal.getText(row, col));
			cal.datefield.date.setDate(day.intValue());
			cal.datefield.client.updateVariable(cal.datefield.id, "day", cal.datefield.date.getDate(), cal.datefield.immediate);
			
			// No need to update calendar header
			cal.clearCalendarBody(false);
			cal.buildCalendarBody();
		}
		
	}
}
