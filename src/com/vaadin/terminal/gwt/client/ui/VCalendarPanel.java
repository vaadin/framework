/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.LocaleService;

public class VCalendarPanel extends FlexTable implements MouseListener {

    private final VDateField datefield;

    private VEventButton prevYear;

    private VEventButton nextYear;

    private VEventButton prevMonth;

    private VEventButton nextMonth;

    private VTime time;

    private Date minDate = null;

    private Date maxDate = null;

    private CalendarEntrySource entrySource;

    private FlexTable days = new FlexTable();

    /* Needed to identify resolution changes */
    private int resolution = VDateField.RESOLUTION_YEAR;

    /* Needed to identify locale changes */
    private String locale = LocaleService.getDefaultLocale();

    public VCalendarPanel(VDateField parent) {
        datefield = parent;
        setStyleName(VDateField.CLASSNAME + "-calendarpanel");
        // buildCalendar(true);
        days.addClickHandler(new DateClickHandler(this));
    }

    public VCalendarPanel(VDateField parent, Date min, Date max) {
        datefield = parent;
        setStyleName(VDateField.CLASSNAME + "-calendarpanel");
        days.addClickHandler(new DateClickHandler(this));

    }

    private void buildCalendar(boolean forceRedraw) {
        final boolean needsMonth = datefield.getCurrentResolution() > VDateField.RESOLUTION_YEAR;
        boolean needsBody = datefield.getCurrentResolution() >= VDateField.RESOLUTION_DAY;
        final boolean needsTime = datefield.getCurrentResolution() >= VDateField.RESOLUTION_HOUR;
        forceRedraw = prevYear == null ? true : forceRedraw;
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
            // Leave the cells in place but clear their contents

            // This has the side effect of ensuring that the calendar always
            // contain 7 rows.
            for (int row = 1; row < 7; row++) {
                for (int col = 0; col < 8; col++) {
                    days.setHTML(row, col, "&nbsp;");
                }
            }
        } else if (getRowCount() > 1) {
            removeRow(1);
            days.clear();

        }
    }

    private void buildCalendarHeader(boolean forceRedraw, boolean needsMonth) {
        if (forceRedraw) {
            if (prevMonth == null) {

                getFlexCellFormatter().setStyleName(0, 0,
                        VDateField.CLASSNAME + "-calendarpanel-prevyear");
                getFlexCellFormatter().setStyleName(0, 4,
                        VDateField.CLASSNAME + "-calendarpanel-nextyear");
                getFlexCellFormatter().setStyleName(0, 3,
                        VDateField.CLASSNAME + "-calendarpanel-nextmonth");
                getFlexCellFormatter().setStyleName(0, 1,
                        VDateField.CLASSNAME + "-calendarpanel-prevmonth");

                getRowFormatter().addStyleName(0,
                        VDateField.CLASSNAME + "-calendarpanel-header");

                prevYear = new VEventButton();
                prevYear.setHTML("&laquo;");
                prevYear.setStyleName("v-button-prevyear");
                nextYear = new VEventButton();
                nextYear.setHTML("&raquo;");
                nextYear.setStyleName("v-button-nextyear");
                prevYear.addMouseListener(this);
                nextYear.addMouseListener(this);
                setWidget(0, 0, prevYear);
                setWidget(0, 4, nextYear);

                if (needsMonth) {
                    prevMonth = new VEventButton();
                    prevMonth.setHTML("&lsaquo;");
                    prevMonth.setStyleName("v-button-prevmonth");
                    nextMonth = new VEventButton();
                    nextMonth.setHTML("&rsaquo;");
                    nextMonth.setStyleName("v-button-nextmonth");
                    prevMonth.addMouseListener(this);
                    nextMonth.addMouseListener(this);
                    setWidget(0, 3, nextMonth);
                    setWidget(0, 1, prevMonth);
                }
            } else if (!needsMonth) {
                // Remove month traverse buttons
                prevMonth.removeMouseListener(this);
                nextMonth.removeMouseListener(this);
                remove(prevMonth);
                remove(nextMonth);
                prevMonth = null;
                nextMonth = null;
            }
        }

        final String monthName = needsMonth ? datefield.getDateTimeService()
                .getMonth(datefield.getShowingDate().getMonth()) : "";
        final int year = datefield.getShowingDate().getYear() + 1900;
        getFlexCellFormatter().setStyleName(0, 2,
                VDateField.CLASSNAME + "-calendarpanel-month");
        setHTML(0, 2, "<span class=\"" + VDateField.CLASSNAME
                + "-calendarpanel-month\">" + monthName + " " + year
                + "</span>");
    }

    private void buildCalendarBody() {

        final boolean showISOWeekNumbers = datefield.isShowISOWeekNumbers();
        final int weekColumn = 0;
        final int firstWeekdayColumn = 1;
        final int headerRow = 0;

        setWidget(1, 0, days);
        setCellPadding(0);
        setCellSpacing(0);
        getFlexCellFormatter().setColSpan(1, 0, 5);
        getFlexCellFormatter().setStyleName(1, 0,
                VDateField.CLASSNAME + "-calendarpanel-body");

        days.getFlexCellFormatter().setStyleName(headerRow, weekColumn,
                "v-week");
        days.setHTML(headerRow, weekColumn, "<strong></strong>");
        // Hide the week column if week numbers are not to be displayed.
        days.getFlexCellFormatter().setVisible(headerRow, weekColumn,
                showISOWeekNumbers);

        days.getRowFormatter().setStyleName(headerRow,
                VDateField.CLASSNAME + "-calendarpanel-weekdays");

        if (showISOWeekNumbers) {
            days.getFlexCellFormatter().setStyleName(headerRow, weekColumn,
                    "v-first");
            days.getFlexCellFormatter().setStyleName(headerRow,
                    firstWeekdayColumn, "");
            days.getRowFormatter().addStyleName(headerRow,
                    VDateField.CLASSNAME + "-calendarpanel-weeknumbers");
        } else {
            days.getFlexCellFormatter().setStyleName(headerRow, weekColumn, "");
            days.getFlexCellFormatter().setStyleName(headerRow,
                    firstWeekdayColumn, "v-first");
        }

        days.getFlexCellFormatter().setStyleName(headerRow,
                firstWeekdayColumn + 6, "v-last");

        // Print weekday names
        final int firstDay = datefield.getDateTimeService().getFirstDayOfWeek();
        for (int i = 0; i < 7; i++) {
            int day = i + firstDay;
            if (day > 6) {
                day = 0;
            }
            if (datefield.getCurrentResolution() > VDateField.RESOLUTION_MONTH) {
                days.setHTML(headerRow, firstWeekdayColumn + i, "<strong>"
                        + datefield.getDateTimeService().getShortDay(day)
                        + "</strong>");
            } else {
                days.setHTML(headerRow, firstWeekdayColumn + i, "");
            }
        }

        // date actually selected?
        Date selectedDate = datefield.getCurrentDate();
        // Showing is the date (year/month+year) that is currently shown in the
        // panel
        Date showing = datefield.getShowingDate();

        // The day of month that is selected, -1 if no day of this month is
        // selected (i.e, showing another month/year than selected or nothing is
        // selected)
        int dayOfMonthSelected = -1;
        // The day of month that is today, -1 if no day of this month is today
        // (i.e., showing another month/year than current)
        int dayOfMonthToday = -1;

        // Find out a day this month is selected
        if (selectedDate != null
                && selectedDate.getMonth() == showing.getMonth()
                && selectedDate.getYear() == showing.getYear()) {
            dayOfMonthSelected = selectedDate.getDate();
        }

        // Find out if today is in this month
        final Date today = new Date();
        if (today.getMonth() == showing.getMonth()
                && today.getYear() == showing.getYear()) {
            dayOfMonthToday = today.getDate();
        }

        final int startWeekDay = datefield.getDateTimeService()
                .getStartWeekDay(showing);
        final int daysInMonth = DateTimeService.getNumberOfDaysInMonth(showing);

        int dayCount = 0;
        final Date curr = new Date(showing.getTime());

        // No month has more than 6 weeks so 6 is a safe maximum for rows.
        for (int weekOfMonth = 1; weekOfMonth < 7; weekOfMonth++) {
            boolean weekNumberProcessed[] = new boolean[] { false, false,
                    false, false, false, false, false };

            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                if (!(weekOfMonth == 1 && dayOfWeek < startWeekDay)) {

                    if (dayCount >= daysInMonth) {
                        // All days printed and we are done
                        break;
                    }

                    final int dayOfMonth = ++dayCount;
                    final String baseclass = VDateField.CLASSNAME
                            + "-calendarpanel-day";

                    String title = "";
                    curr.setDate(dayCount);

                    if (entrySource != null) {
                        final List entries = entrySource.getEntries(curr,
                                VDateField.RESOLUTION_DAY);
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

                    // Add CSS classes according to state
                    String cssClass = baseclass;

                    if (!isEnabledDate(curr)) {
                        cssClass += " " + baseclass + "-disabled";
                    }

                    if (dayOfMonthSelected == dayOfMonth) {
                        cssClass += " " + baseclass + "-selected";
                    }

                    if (dayOfMonthToday == dayOfMonth) {
                        cssClass += " " + baseclass + "-today";
                    }
                    if (title.length() > 0) {
                        cssClass += " " + baseclass + "-entry";
                    }

                    // Actually write the day of month
                    days.setHTML(weekOfMonth, firstWeekdayColumn + dayOfWeek,
                            "<span title=\"" + title + "\" class=\"" + cssClass
                                    + "\">" + dayOfMonth + "</span>");

                    // ISO week numbers if requested
                    if (!weekNumberProcessed[weekOfMonth]) {
                        days.getCellFormatter().setVisible(weekOfMonth,
                                weekColumn, showISOWeekNumbers);
                        if (showISOWeekNumbers) {
                            final String baseCssClass = VDateField.CLASSNAME
                                    + "-calendarpanel-weeknumber";
                            String weekCssClass = baseCssClass;

                            int weekNumber = DateTimeService
                                    .getISOWeekNumber(curr);

                            days.setHTML(weekOfMonth, 0, "<span class=\""
                                    + weekCssClass + "\"" + ">" + weekNumber
                                    + "</span>");
                            weekNumberProcessed[weekOfMonth] = true;
                        }

                    }
                }
            }
        }

    }

    private void buildTime(boolean forceRedraw) {
        if (time == null) {
            time = new VTime(datefield);
            setWidget(2, 0, time);
            getFlexCellFormatter().setColSpan(2, 0, 5);
            getFlexCellFormatter().setStyleName(2, 0,
                    VDateField.CLASSNAME + "-calendarpanel-time");
        }
        time.updateTime(forceRedraw);
    }

    /**
     * 
     * @param forceRedraw
     *            Build all from scratch, in case of e.g. locale changes
     */
    public void updateCalendar() {
        // Locale and resolution changes force a complete redraw
        buildCalendar(locale != datefield.getCurrentLocale()
                || resolution != datefield.getCurrentResolution());
        if (datefield instanceof VTextualDate) {
            ((VTextualDate) datefield).buildDate();
        }
        locale = datefield.getCurrentLocale();
        resolution = datefield.getCurrentResolution();
    }

    private boolean isEnabledDate(Date date) {
        if ((minDate != null && date.before(minDate))
                || (maxDate != null && date.after(maxDate))) {
            return false;
        }
        return true;
    }

    private void processClickEvent(Widget sender, boolean updateVariable) {
        if (!datefield.isEnabled() || datefield.isReadonly()) {
            return;
        }
        Date showingDate = datefield.getShowingDate();
        if (!updateVariable) {
            if (sender == prevYear) {
                showingDate.setYear(showingDate.getYear() - 1);
                updateCalendar();
            } else if (sender == nextYear) {
                showingDate.setYear(showingDate.getYear() + 1);
                updateCalendar();
            } else if (sender == prevMonth) {
                int currentMonth = showingDate.getMonth();
                showingDate.setMonth(currentMonth - 1);

                /*
                 * If the selected date was e.g. 31.12 the new date would be
                 * 31.11 but this date is invalid so the new date will be 1.12.
                 * This is taken care of by decreasing the date until we have
                 * the correct month.
                 */
                while (showingDate.getMonth() == currentMonth) {
                    showingDate.setDate(showingDate.getDate() - 1);
                }

                updateCalendar();
            } else if (sender == nextMonth) {
                int currentMonth = showingDate.getMonth();
                showingDate.setMonth(currentMonth + 1);
                int requestedMonth = (currentMonth + 1) % 12;

                /*
                 * If the selected date was e.g. 31.3 the new date would be 31.4
                 * but this date is invalid so the new date will be 1.5. This is
                 * taken care of by decreasing the date until we have the
                 * correct month.
                 */
                while (showingDate.getMonth() != requestedMonth) {
                    showingDate.setDate(showingDate.getDate() - 1);
                }

                updateCalendar();
            }
        } else {
            if (datefield.getCurrentResolution() == VDateField.RESOLUTION_YEAR
                    || datefield.getCurrentResolution() == VDateField.RESOLUTION_MONTH) {
                // Due to current UI, update variable if res=year/month
                datefield.setCurrentDate(new Date(showingDate.getTime()));
                if (datefield.getCurrentResolution() == VDateField.RESOLUTION_MONTH) {
                    datefield.getClient().updateVariable(datefield.getId(),
                            "month", datefield.getCurrentDate().getMonth() + 1,
                            false);
                }
                datefield.getClient().updateVariable(datefield.getId(), "year",
                        datefield.getCurrentDate().getYear() + 1900,
                        datefield.isImmediate());

                /* Must update the value in the textfield also */
                updateCalendar();
            }
        }
    }

    private Timer timer;

    public void onMouseDown(final Widget sender, int x, int y) {
        // Allow user to click-n-hold for fast-forward or fast-rewind.
        // Timer is first used for a 500ms delay after mousedown. After that has
        // elapsed, another timer is triggered to go off every 150ms. Both
        // timers are cancelled on mouseup or mouseout.
        if (sender instanceof VEventButton) {
            processClickEvent(sender, false);
            timer = new Timer() {
                @Override
                public void run() {
                    timer = new Timer() {
                        @Override
                        public void run() {
                            processClickEvent(sender, false);
                        }
                    };
                    timer.scheduleRepeating(150);
                }
            };
            timer.schedule(500);
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
        processClickEvent(sender, true);
    }

    private class VEventButton extends VNativeButton implements
            SourcesMouseEvents {

        private MouseListenerCollection mouseListeners;

        public VEventButton() {
            super();
            sinkEvents(Event.FOCUSEVENTS | Event.KEYEVENTS | Event.ONCLICK
                    | Event.MOUSEEVENTS);
        }

        @Override
        public void addMouseListener(MouseListener listener) {
            if (mouseListeners == null) {
                mouseListeners = new MouseListenerCollection();
            }
            mouseListeners.add(listener);
        }

        @Override
        public void removeMouseListener(MouseListener listener) {
            if (mouseListeners != null) {
                mouseListeners.remove(listener);
            }
        }

        @Override
        public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            switch (DOM.eventGetType(event)) {
            case Event.ONMOUSEDOWN:
            case Event.ONMOUSEUP:
            case Event.ONMOUSEOUT:
                if (mouseListeners != null) {
                    mouseListeners.fireMouseEvent(this, event);
                }
                break;
            }
        }
    }

    private class DateClickHandler implements ClickHandler {

        private final VCalendarPanel cal;

        public DateClickHandler(VCalendarPanel panel) {
            cal = panel;
        }

        public void onClick(ClickEvent event) {
            Object sender = event.getSource();
            Cell cell = cal.days.getCellForEvent(event);
            if (sender != cal.days || cell == null || cell.getRowIndex() < 1
                    || cell.getRowIndex() > 6 || !cal.datefield.isEnabled()
                    || cal.datefield.isReadonly() || cell.getCellIndex() < 1) {
                return;
            }

            final String text = cal.days.getText(cell.getRowIndex(), cell
                    .getCellIndex());
            if (text.equals(" ")) {
                return;
            }

            try {
                final Integer day = new Integer(text);
                final Date newDate = cal.datefield.getShowingDate();
                newDate.setDate(day.intValue());
                if (!isEnabledDate(newDate)) {
                    return;
                }
                if (cal.datefield.getCurrentDate() == null) {
                    cal.datefield.setCurrentDate(new Date(newDate.getTime()));

                    // Init variables with current time
                    datefield.getClient().updateVariable(cal.datefield.getId(),
                            "hour", newDate.getHours(), false);
                    datefield.getClient().updateVariable(cal.datefield.getId(),
                            "min", newDate.getMinutes(), false);
                    datefield.getClient().updateVariable(cal.datefield.getId(),
                            "sec", newDate.getSeconds(), false);
                    datefield.getClient().updateVariable(cal.datefield.getId(),
                            "msec", datefield.getMilliseconds(), false);
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

                if (datefield instanceof VTextualDate
                        && resolution < VDateField.RESOLUTION_HOUR) {
                    ((VOverlay) getParent()).hide();
                } else {
                    updateCalendar();
                }

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

    /**
     * Sets focus to Calendar panel.
     * 
     * @param focus
     */
    public void setFocus(boolean focus) {
        nextYear.setFocus(focus);
    }

}
