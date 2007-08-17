package com.itmill.toolkit.terminal.gwt.client.ui.datefield;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import com.itmill.toolkit.terminal.gwt.client.ui.IButton;
import com.itmill.toolkit.terminal.gwt.client.ui.IDateField;
import com.itmill.toolkit.terminal.gwt.client.ui.ITextualDate;
import com.itmill.toolkit.terminal.gwt.client.ui.calendar.ICalendarEntry;

public class ICalendarPanel extends FlexTable implements MouseListener,
	ClickListener {

    private IDateField datefield;

    private IEventButton prevYear;
    private IEventButton nextYear;
    private IEventButton prevMonth;
    private IEventButton nextMonth;

    private ITime time;

    private Date minDate = null;
    private Date maxDate = null;

    private CalendarEntrySource entrySource;

    /* Needed to identify resolution changes */
    private int resolution = IDateField.RESOLUTION_YEAR;

    /* Needed to identify locale changes */
    private String locale = LocaleService.getDefaultLocale();

    public ICalendarPanel(IDateField parent) {
	datefield = parent;
	setStyleName(datefield.CLASSNAME + "-calendarpanel");
	// buildCalendar(true);
	addTableListener(new DateClickListener(this));
    }

    public ICalendarPanel(IDateField parent, Date min, Date max) {
	datefield = parent;
	setStyleName(datefield.CLASSNAME + "-calendarpanel");
	// buildCalendar(true);
	addTableListener(new DateClickListener(this));
    }

    private void buildCalendar(boolean forceRedraw) {
	boolean needsMonth = datefield.getCurrentResolution() > IDateField.RESOLUTION_YEAR;
	boolean needsBody = datefield.getCurrentResolution() >= IDateField.RESOLUTION_DAY;
	boolean needsTime = datefield.getCurrentResolution() >= IDateField.RESOLUTION_HOUR;
	buildCalendarHeader(forceRedraw, needsMonth);
	clearCalendarBody(!needsBody);
	if (needsBody)
	    buildCalendarBody();
	if (needsTime)
	    buildTime(forceRedraw);
	else if (time != null) {
	    remove(time);
	    time = null;
	}
    }

    private void clearCalendarBody(boolean remove) {
	if (!remove) {
	    for (int row = 2; row < 8; row++) {
		for (int col = 0; col < 7; col++) {
		    setHTML(row, col, "&nbsp;");
		}
	    }
	} else if (getRowCount() > 2) {
	    while (getRowCount() > 2)
		removeRow(2);
	}
    }

    private void buildCalendarHeader(boolean forceRedraw, boolean needsMonth) {
	// Can't draw a calendar without a date :)
	if (datefield.getCurrentDate() == null)
	    datefield.setCurrentDate(new Date());

	if (forceRedraw) {
	    if (prevMonth == null) { // Only do once
		prevYear = new IEventButton();
		prevYear.setHTML("&laquo;");
		nextYear = new IEventButton();
		nextYear.setHTML("&raquo;");
		prevYear.addMouseListener(this);
		nextYear.addMouseListener(this);
		prevYear.addClickListener(this);
		nextYear.addClickListener(this);
		setWidget(0, 0, prevYear);
		setWidget(0, 4, nextYear);

		if (needsMonth) {
		    prevMonth = new IEventButton();
		    prevMonth.setHTML("&lsaquo;");
		    nextMonth = new IEventButton();
		    nextMonth.setHTML("&rsaquo;");
		    prevMonth.addMouseListener(this);
		    nextMonth.addMouseListener(this);
		    prevMonth.addClickListener(this);
		    nextMonth.addClickListener(this);
		    setWidget(0, 3, nextMonth);
		    setWidget(0, 1, prevMonth);
		}

		getFlexCellFormatter().setColSpan(0, 2, 3);
	    } else if (!needsMonth) {
		// Remove month traverse buttons
		prevMonth.removeClickListener(this);
		prevMonth.removeMouseListener(this);
		nextMonth.removeClickListener(this);
		nextMonth.removeMouseListener(this);
		remove(prevMonth);
		remove(nextMonth);
		prevMonth = null;
		nextMonth = null;
	    }

	    // Print weekday names
	    int firstDay = datefield.getDateTimeService().getFirstDayOfWeek();
	    for (int i = 0; i < 7; i++) {
		int day = i + firstDay;
		if (day > 6)
		    day = 0;
		if (datefield.getCurrentResolution() > IDateField.RESOLUTION_MONTH)
		    setHTML(1, i, "<strong>"
			    + datefield.getDateTimeService().getShortDay(day)
			    + "</strong>");
		else
		    setHTML(1, i, "");
	    }
	}

	String monthName = needsMonth ? datefield.getDateTimeService()
		.getMonth(datefield.getCurrentDate().getMonth()) : "";
	int year = datefield.getCurrentDate().getYear() + 1900;
	setHTML(0, 2, "<span class=\"" + datefield.CLASSNAME
		+ "-calendarpanel-month\">" + monthName + " " + year
		+ "</span>");
    }

    private void buildCalendarBody() {
	Date date = datefield.getCurrentDate();
	if (date == null)
	    date = new Date();
	int startWeekDay = datefield.getDateTimeService().getStartWeekDay(date);
	int numDays = DateTimeService.getNumberOfDaysInMonth(date);
	int dayCount = 0;
	Date today = new Date();
	Date curr = new Date(date.getTime());
	for (int row = 2; row < 8; row++) {
	    for (int col = 0; col < 7; col++) {
		if (!(row == 2 && col < startWeekDay)) {
		    if (dayCount < numDays) {
			int selectedDate = ++dayCount;
			String title = "";
			if (this.entrySource != null) {
			    curr.setDate(dayCount);
			    List entries = this.entrySource.getEntries(curr,
				    IDateField.RESOLUTION_DAY);
			    if (entries != null) {
				for (Iterator it = entries.iterator(); it
					.hasNext();) {
				    ICalendarEntry entry = (ICalendarEntry) it
					    .next();
				    title += (title.length() > 0 ? ", " : "")
					    + entry.getStringForDate(curr);
				}
			    }
			}
			String baseclass = datefield.CLASSNAME
				+ "-calendarpanel-day";
			String cssClass = baseclass;
			if (!isEnabledDate(curr)) {
			    cssClass += " " + baseclass + "-disabled";
			}
			if (date.getDate() == dayCount) {
			    cssClass += " " + baseclass + "-selected";
			}
			if (today.getDate() == dayCount
				&& today.getMonth() == date.getMonth()
				&& today.getYear() == date.getYear()) {
			    cssClass += " " + baseclass + "-today";
			}
			if (title.length() > 0)
			    cssClass += " " + baseclass + "-entry";
			setHTML(row, col, "<span title=\"" + title
				+ "\" class=\"" + cssClass + "\">"
				+ selectedDate + "</span>");
		    } else {
			break;
		    }

		}
	    }
	}
    }

    private void buildTime(boolean forceRedraw) {
	if (time == null) {
	    time = new ITime(datefield);
	    setText(8, 0, ""); // Add new row
	    getFlexCellFormatter().setColSpan(8, 0, 7);
	    setWidget(8, 0, time);
	}
	time.updateTime(forceRedraw);
    }

    /**
     * 
     * @param forceRedraw
     *                Build all from scratch, in case of e.g. locale changes
     */
    public void updateCalendar() {
	// Locale and resolution changes force a complete redraw
	buildCalendar(locale != datefield.getCurrentLocale()
		|| resolution != datefield.getCurrentResolution());
	if (datefield instanceof ITextualDate)
	    ((ITextualDate) datefield).buildDate();
	locale = datefield.getCurrentLocale();
	resolution = datefield.getCurrentResolution();
    }

    public void onClick(Widget sender) {
	processClickEvent(sender);
    }

    private boolean isEnabledDate(Date date) {
	if ((this.minDate != null && date.before(this.minDate))
		|| (this.maxDate != null && date.after(this.maxDate))) {
	    return false;
	}
	return true;
    }

    private void processClickEvent(Widget sender) {
	if (!datefield.isEnabled() || datefield.isReadonly())
	    return;

	if (sender == prevYear) {
	    datefield.getCurrentDate().setYear(datefield.getCurrentDate().getYear() - 1);
	    datefield.getClient().updateVariable(datefield.getId(), "year",
		    datefield.getCurrentDate().getYear() + 1900, datefield.isImmediate());
	    updateCalendar();
	} else if (sender == nextYear) {
	    datefield.getCurrentDate().setYear(datefield.getCurrentDate().getYear() + 1);
	    datefield.getClient().updateVariable(datefield.getId(), "year",
		    datefield.getCurrentDate().getYear() + 1900, datefield.isImmediate());
	    updateCalendar();
	} else if (sender == prevMonth) {
	    datefield.getCurrentDate().setMonth(datefield.getCurrentDate().getMonth() - 1);
	    datefield.getClient().updateVariable(datefield.getId(), "month",
		    datefield.getCurrentDate().getMonth() + 1, datefield.isImmediate());
	    updateCalendar();
	} else if (sender == nextMonth) {
	    datefield.getCurrentDate().setMonth(datefield.getCurrentDate().getMonth() + 1);
	    datefield.getClient().updateVariable(datefield.getId(), "month",
		    datefield.getCurrentDate().getMonth() + 1, datefield.isImmediate());
	    updateCalendar();
	}
    }

    private Timer timer;

    public void onMouseDown(final Widget sender, int x, int y) {
	if (sender instanceof IEventButton) {
	    timer = new Timer() {
		public void run() {
		    processClickEvent(sender);
		}
	    };
	    timer.scheduleRepeating(100);
	}
    }

    public void onMouseEnter(Widget sender) {
    }

    public void onMouseLeave(Widget sender) {
	if (timer != null)
	    timer.cancel();
    }

    public void onMouseMove(Widget sender, int x, int y) {
    }

    public void onMouseUp(Widget sender, int x, int y) {
	if (timer != null)
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
	    if (sender != cal || row < 2 || row > 7 || !cal.datefield.isEnabled()
		    || cal.datefield.isReadonly())
		return;

	    String text = cal.getText(row, col);
	    if (text.equals(" "))
		return;

	    Integer day = new Integer(text);

	    Date newDate = new Date(cal.datefield.getCurrentDate().getTime());
	    newDate.setDate(day.intValue());
	    if (!isEnabledDate(newDate)) {
		return;
	    }
	    cal.datefield.getCurrentDate().setTime(newDate.getTime());
	    cal.datefield.getClient().updateVariable(cal.datefield.getId(), "day",
		    cal.datefield.getCurrentDate().getDate(), cal.datefield.isImmediate());

	    updateCalendar();
	}

    }

    public void setLimits(Date min, Date max) {
	if (min != null) {
	    Date d = new Date(min.getTime());
	    d.setHours(0);
	    d.setMinutes(0);
	    d.setSeconds(1);
	    this.minDate = d;
	} else {
	    this.minDate = null;
	}
	if (max != null) {
	    Date d = new Date(max.getTime());
	    d.setHours(24);
	    d.setMinutes(59);
	    d.setSeconds(59);
	    this.maxDate = d;
	} else {
	    this.maxDate = null;
	}
    }

    public void setCalendarEntrySource(CalendarEntrySource entrySource) {
	this.entrySource = entrySource;
    }

    public CalendarEntrySource getCalendarEntrySource() {
	return this.entrySource;
    }

    public interface CalendarEntrySource {
	public List getEntries(Date date, int resolution);
    }

}
