/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.itmill.toolkit.terminal.gwt.client.ui;

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

public class CalendarPanel extends FlexTable implements MouseListener,
        ClickListener {

    private final IDateField datefield;

    private IEventButton prevYear;

    private IEventButton nextYear;

    private IEventButton prevMonth;

    private IEventButton nextMonth;

    private Time time;

    private Date minDate = null;

    private Date maxDate = null;

    private CalendarEntrySource entrySource;

    /* Needed to identify resolution changes */
    private int resolution = IDateField.RESOLUTION_YEAR;

    /* Needed to identify locale changes */
    private String locale = LocaleService.getDefaultLocale();

    public CalendarPanel(IDateField parent) {
        datefield = parent;
        setStyleName(IDateField.CLASSNAME + "-calendarpanel");
        // buildCalendar(true);
        addTableListener(new DateClickListener(this));
    }

    public CalendarPanel(IDateField parent, Date min, Date max) {
        datefield = parent;
        setStyleName(IDateField.CLASSNAME + "-calendarpanel");
        // buildCalendar(true);
        addTableListener(new DateClickListener(this));

    }

    private void buildCalendar(boolean forceRedraw) {
        final boolean needsMonth = datefield.getCurrentResolution() > IDateField.RESOLUTION_YEAR;
        boolean needsBody = datefield.getCurrentResolution() >= IDateField.RESOLUTION_DAY;
        final boolean needsTime = datefield.getCurrentResolution() >= IDateField.RESOLUTION_HOUR;
        buildCalendarHeader(forceRedraw, needsMonth);
        clearCalendarBody(!needsBody);
        if (needsBody) {
            buildCalendarBody();
        }
        if (needsTime) {
            buildTime(forceRedraw);
        } else if (time != null) {
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
            while (getRowCount() > 2) {
                removeRow(2);
            }
        }
    }

    private void buildCalendarHeader(boolean forceRedraw, boolean needsMonth) {
        if (forceRedraw) {
            if (prevMonth == null) { // Only do once
                prevYear = new IEventButton();
                prevYear.setHTML("&laquo;");
                prevYear.setStyleName("i-button-prevyear");
                nextYear = new IEventButton();
                nextYear.setHTML("&raquo;");
                nextYear.setStyleName("i-button-nextyear");
                prevYear.addMouseListener(this);
                nextYear.addMouseListener(this);
                prevYear.addClickListener(this);
                nextYear.addClickListener(this);
                setWidget(0, 0, prevYear);
                setWidget(0, 4, nextYear);

                if (needsMonth) {
                    prevMonth = new IEventButton();
                    prevMonth.setHTML("&lsaquo;");
                    prevMonth.setStyleName("i-button-prevmonth");
                    nextMonth = new IEventButton();
                    nextMonth.setHTML("&rsaquo;");
                    nextMonth.setStyleName("i-button-nextmonth");
                    prevMonth.addMouseListener(this);
                    nextMonth.addMouseListener(this);
                    prevMonth.addClickListener(this);
                    nextMonth.addClickListener(this);
                    setWidget(0, 3, nextMonth);
                    setWidget(0, 1, prevMonth);
                }

                getFlexCellFormatter().setColSpan(0, 2, 3);
                getRowFormatter().addStyleName(0,
                        IDateField.CLASSNAME + "-calendarpanel-header");
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
            final int firstDay = datefield.getDateTimeService()
                    .getFirstDayOfWeek();
            for (int i = 0; i < 7; i++) {
                int day = i + firstDay;
                if (day > 6) {
                    day = 0;
                }
                if (datefield.getCurrentResolution() > IDateField.RESOLUTION_MONTH) {
                    setHTML(1, i, "<strong>"
                            + datefield.getDateTimeService().getShortDay(day)
                            + "</strong>");
                } else {
                    setHTML(1, i, "");
                }
            }
        }

        final String monthName = needsMonth ? datefield.getDateTimeService()
                .getMonth(datefield.getShowingDate().getMonth()) : "";
        final int year = datefield.getShowingDate().getYear() + 1900;
        setHTML(0, 2, "<span class=\"" + IDateField.CLASSNAME
                + "-calendarpanel-month\">" + monthName + " " + year
                + "</span>");
    }

    private void buildCalendarBody() {
        // date actually selected?
        Date currentDate = datefield.getCurrentDate();
        Date showing = datefield.getShowingDate();
        boolean selected = (currentDate != null
                && currentDate.getMonth() == showing.getMonth() && currentDate
                .getYear() == showing.getYear());

        final int startWeekDay = datefield.getDateTimeService()
                .getStartWeekDay(datefield.getShowingDate());
        final int numDays = DateTimeService.getNumberOfDaysInMonth(datefield
                .getShowingDate());
        int dayCount = 0;
        final Date today = new Date();
        final Date curr = new Date(datefield.getShowingDate().getTime());
        for (int row = 2; row < 8; row++) {
            for (int col = 0; col < 7; col++) {
                if (!(row == 2 && col < startWeekDay)) {
                    if (dayCount < numDays) {
                        final int selectedDate = ++dayCount;
                        String title = "";
                        if (entrySource != null) {
                            curr.setDate(dayCount);
                            final List entries = entrySource.getEntries(curr,
                                    IDateField.RESOLUTION_DAY);
                            if (entries != null) {
                                for (final Iterator it = entries.iterator(); it
                                        .hasNext();) {
                                    final CalendarEntry entry = (CalendarEntry) it
                                            .next();
                                    title += (title.length() > 0 ? ", " : "")
                                            + entry.getStringForDate(curr);
                                }
                            }
                        }
                        final String baseclass = IDateField.CLASSNAME
                                + "-calendarpanel-day";
                        String cssClass = baseclass;
                        if (!isEnabledDate(curr)) {
                            cssClass += " " + baseclass + "-disabled";
                        }
                        if (selected
                                && datefield.getShowingDate().getDate() == dayCount) {
                            cssClass += " " + baseclass + "-selected";
                        }
                        if (today.getDate() == dayCount
                                && today.getMonth() == datefield
                                        .getShowingDate().getMonth()
                                && today.getYear() == datefield
                                        .getShowingDate().getYear()) {
                            cssClass += " " + baseclass + "-today";
                        }
                        if (title.length() > 0) {
                            cssClass += " " + baseclass + "-entry";
                        }
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
            time = new Time(datefield);
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
        if (datefield instanceof ITextualDate) {
            ((ITextualDate) datefield).buildDate();
        }
        locale = datefield.getCurrentLocale();
        resolution = datefield.getCurrentResolution();
    }

    public void onClick(Widget sender) {
        processClickEvent(sender);
    }

    private boolean isEnabledDate(Date date) {
        if ((minDate != null && date.before(minDate))
                || (maxDate != null && date.after(maxDate))) {
            return false;
        }
        return true;
    }

    private void processClickEvent(Widget sender) {
        if (!datefield.isEnabled() || datefield.isReadonly()) {
            return;
        }
        Date showingDate = datefield.getShowingDate();
        if (sender == prevYear) {
            showingDate.setYear(showingDate.getYear() - 1);
            updateCalendar();
        } else if (sender == nextYear) {
            showingDate.setYear(showingDate.getYear() + 1);
            updateCalendar();
        } else if (sender == prevMonth) {
            showingDate.setMonth(showingDate.getMonth() - 1);
            updateCalendar();
        } else if (sender == nextMonth) {
            showingDate.setMonth(showingDate.getMonth() + 1);
            updateCalendar();
        }
        if (datefield.getCurrentResolution() == IDateField.RESOLUTION_YEAR
                || datefield.getCurrentResolution() == IDateField.RESOLUTION_MONTH) {
            // Due to current UI, update variable if res=year/month
            datefield.setCurrentDate(new Date(showingDate.getTime()));
            if (datefield.getCurrentResolution() == IDateField.RESOLUTION_MONTH) {
                datefield.getClient().updateVariable(datefield.getId(),
                        "month", datefield.getCurrentDate().getMonth() + 1,
                        false);
            }
            datefield.getClient().updateVariable(datefield.getId(), "year",
                    datefield.getCurrentDate().getYear() + 1900,
                    datefield.isImmediate());
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
        if (timer != null) {
            timer.cancel();
        }
    }

    public void onMouseMove(Widget sender, int x, int y) {
    }

    public void onMouseUp(Widget sender, int x, int y) {
        if (timer != null) {
            timer.cancel();
        }
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
            if (mouseListeners != null) {
                mouseListeners.remove(listener);
            }
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

        private final CalendarPanel cal;

        public DateClickListener(CalendarPanel panel) {
            cal = panel;
        }

        public void onCellClicked(SourcesTableEvents sender, int row, int col) {
            if (sender != cal || row < 2 || row > 7
                    || !cal.datefield.isEnabled() || cal.datefield.isReadonly()) {
                return;
            }

            final String text = cal.getText(row, col);
            if (text.equals(" ")) {
                return;
            }

            try {
                final Integer day = new Integer(text);
                if (cal.datefield.getCurrentDate() == null) {
                    cal.datefield.setCurrentDate(new Date());
                }
                final Date newDate = cal.datefield.getShowingDate();
                newDate.setDate(day.intValue());
                if (!isEnabledDate(newDate)) {
                    return;
                }
                cal.datefield.getCurrentDate().setTime(newDate.getTime());
                cal.datefield.getClient().updateVariable(cal.datefield.getId(),
                        "day", cal.datefield.getCurrentDate().getDate(), false);
                cal.datefield.getClient().updateVariable(cal.datefield.getId(),
                        "month", cal.datefield.getCurrentDate().getMonth() + 1,
                        false);
                cal.datefield.getClient().updateVariable(cal.datefield.getId(),
                        "year",
                        cal.datefield.getCurrentDate().getYear() + 1900,
                        cal.datefield.isImmediate());

                updateCalendar();
            } catch (final NumberFormatException e) {
                // Not a number, ignore and stop here
                return;
            }
        }

    }

    public void setLimits(Date min, Date max) {
        if (min != null) {
            final Date d = new Date(min.getTime());
            d.setHours(0);
            d.setMinutes(0);
            d.setSeconds(1);
            minDate = d;
        } else {
            minDate = null;
        }
        if (max != null) {
            final Date d = new Date(max.getTime());
            d.setHours(24);
            d.setMinutes(59);
            d.setSeconds(59);
            maxDate = d;
        } else {
            maxDate = null;
        }
    }

    public void setCalendarEntrySource(CalendarEntrySource entrySource) {
        this.entrySource = entrySource;
    }

    public CalendarEntrySource getCalendarEntrySource() {
        return entrySource;
    }

    public interface CalendarEntrySource {
        public List getEntries(Date date, int resolution);
    }

}
