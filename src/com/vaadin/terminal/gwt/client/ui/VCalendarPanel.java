/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.DateTimeService;
import com.vaadin.terminal.gwt.client.LocaleService;

public class VCalendarPanel extends FocusableFlexTable implements
        MouseListener, KeyDownHandler, KeyPressHandler {

    /**
     * Represents a Date button in the calendar
     */
    private class VEventButton extends VNativeButton implements
            SourcesMouseEvents {

        private MouseListenerCollection mouseListeners;

        /**
         * Default constructor
         */
        public VEventButton() {
            super();
            sinkEvents(Event.FOCUSEVENTS | Event.KEYEVENTS | Event.ONCLICK
                    | Event.MOUSEEVENTS);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.FocusWidget#addMouseListener(com.google
         * .gwt.user.client.ui.MouseListener)
         */
        @Override
        public void addMouseListener(MouseListener listener) {
            if (mouseListeners == null) {
                mouseListeners = new MouseListenerCollection();
            }
            mouseListeners.add(listener);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.user.client.ui.FocusWidget#removeMouseListener(com
         * .google.gwt.user.client.ui.MouseListener)
         */
        @Override
        public void removeMouseListener(MouseListener listener) {
            if (mouseListeners != null) {
                mouseListeners.remove(listener);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.vaadin.terminal.gwt.client.ui.VNativeButton#onBrowserEvent(com
         * .google.gwt.user.client.Event)
         */
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

    /**
     * Represents a click handler for when a user selects a date by using the
     * mouse
     */
    private class DateClickHandler implements ClickHandler {

        private final VCalendarPanel cal;

        public DateClickHandler(VCalendarPanel panel) {
            cal = panel;
        }

        public void selectDate(String date) {
            try {
                final Integer day = new Integer(date);
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

                if (datefield instanceof VTextualDate) {
                    ((VOverlay) getParent()).hide();
                } else {
                    updateCalendar();
                }

            } catch (final NumberFormatException e) {
                // Not a number, ignore and stop here
                return;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
         * .event.dom.client.ClickEvent)
         */
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

            selectDate(text);
        }
    }

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

    private int selectedRow;
    private int selectedColumn;

    private boolean changingView = false;

    private final DateClickHandler dateClickHandler;

    private Timer mouseTimer;

    public VCalendarPanel(VDateField parent) {
        super();

        datefield = parent;
        setStyleName(VDateField.CLASSNAME + "-calendarpanel");
        // buildCalendar(true);

        dateClickHandler = new DateClickHandler(this);
        days.addClickHandler(dateClickHandler);

        /*
         * Firefox auto-repeat works correctly only if we use a key press
         * handler, other browsers handle it correctly when using a key down
         * handler
         */
        if (BrowserInfo.get().isGecko()) {
            addKeyPressHandler(this);
        } else {
            addKeyDownHandler(this);
        }

    }

    public VCalendarPanel(VDateField parent, Date min, Date max) {
        super();

        datefield = parent;
        setStyleName(VDateField.CLASSNAME + "-calendarpanel");

        dateClickHandler = new DateClickHandler(this);
        days.addClickHandler(dateClickHandler);

        /*
         * Firefox auto-repeat works correctly only if we use a key press
         * handler, other browsers handle it correctly when using a key down
         * handler
         */
        if (BrowserInfo.get().isGecko()) {
            addKeyPressHandler(this);
        } else {
            addKeyDownHandler(this);
        }
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
                prevYear.getElement().setTabIndex(-1);
                nextYear = new VEventButton();
                nextYear.setHTML("&raquo;");
                nextYear.setStyleName("v-button-nextyear");
                nextYear.getElement().setTabIndex(-1);
                prevYear.addMouseListener(this);
                nextYear.addMouseListener(this);
                setWidget(0, 0, prevYear);
                setWidget(0, 4, nextYear);

                if (needsMonth) {
                    prevMonth = new VEventButton();
                    prevMonth.setHTML("&lsaquo;");
                    prevMonth.setStyleName("v-button-prevmonth");
                    prevMonth.getElement().setTabIndex(-1);
                    nextMonth = new VEventButton();
                    nextMonth.setHTML("&rsaquo;");
                    nextMonth.setStyleName("v-button-nextmonth");
                    nextMonth.getElement().setTabIndex(-1);
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

        // Always show something
        if (showing == null) {
            showing = new Date();
            datefield.setShowingDate(showing);
        }

        // The day of month that is selected, -1 if no day of this month is
        // selected (i.e, showing another month/year than selected or nothing is
        // selected)
        int dayOfMonthSelected = -1;
        // The day of month that is today, -1 if no day of this month is today
        // (i.e., showing another month/year than current)
        int dayOfMonthToday = -1;

        // Find out a day this month is selected
        if (showing != null) {
            dayOfMonthSelected = showing.getDate();
        } else if (selectedDate != null
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

                    // Actually write the day of month
                    InlineHTML html = new InlineHTML(String.valueOf(dayOfMonth));
                    html.setStylePrimaryName(baseclass);
                    html.setTitle(title);

                    // Add CSS classes according to state
                    if (!isEnabledDate(curr)) {
                        html.addStyleDependentName("disabled");
                    }

                    if (dayOfMonthSelected == dayOfMonth) {
                        html.addStyleDependentName("selected");
                        selectedRow = weekOfMonth;
                        selectedColumn = firstWeekdayColumn + dayOfWeek;
                    }

                    if (dayOfMonthToday == dayOfMonth) {
                        html.addStyleDependentName("today");
                    }
                    if (title.length() > 0) {
                        html.addStyleDependentName("entry");
                    }

                    days.setWidget(weekOfMonth, firstWeekdayColumn + dayOfWeek,
                            html);

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
            time = new VTime(datefield, this);
            setWidget(2, 0, time);
            getFlexCellFormatter().setColSpan(2, 0, 5);
            getFlexCellFormatter().setStyleName(2, 0,
                    VDateField.CLASSNAME + "-calendarpanel-time");
        }
        time.updateTime(forceRedraw);
    }

    /**
     * Updates the calendar and text field with the selected dates *
     */
    public void updateCalendar() {
        updateCalendarOnly();
        if (datefield instanceof VTextualDate) {
            ((VTextualDate) datefield).buildDate();
        }
    }

    /**
     * Updates the popup only, does not affect the text field
     */
    private void updateCalendarOnly() {
        if (!changingView) {
            changingView = true;
            // Locale and resolution changes force a complete redraw
            buildCalendar(locale != datefield.getCurrentLocale()
                    || resolution != datefield.getCurrentResolution());
            locale = datefield.getCurrentLocale();
            resolution = datefield.getCurrentResolution();

            /*
             * Wait a while before releasing the lock, so auto-repeated events
             * isn't processed after the calendar is updated
             */
            Timer changeTimer = new Timer() {
                @Override
                public void run() {
                    changingView = false;
                }
            };
            changeTimer.schedule(100);
        }
    }

    private boolean isEnabledDate(Date date) {
        if ((minDate != null && date.before(minDate))
                || (maxDate != null && date.after(maxDate))) {
            return false;
        }
        return true;
    }

    /**
     * Selects the next month
     */
    private void selectNextMonth() {
        Date showingDate = datefield.getShowingDate();
        int currentMonth = showingDate.getMonth();
        showingDate.setMonth(currentMonth + 1);
        int requestedMonth = (currentMonth + 1) % 12;

        /*
         * If the selected date was e.g. 31.3 the new date would be 31.4 but
         * this date is invalid so the new date will be 1.5. This is taken care
         * of by decreasing the date until we have the correct month.
         */
        while (showingDate.getMonth() != requestedMonth) {
            showingDate.setDate(showingDate.getDate() - 1);
        }

        updateCalendar();
    }

    /**
     * Selects the previous month
     */
    private void selectPreviousMonth() {
        Date showingDate = datefield.getShowingDate();
        int currentMonth = showingDate.getMonth();
        showingDate.setMonth(currentMonth - 1);

        /*
         * If the selected date was e.g. 31.12 the new date would be 31.11 but
         * this date is invalid so the new date will be 1.12. This is taken care
         * of by decreasing the date until we have the correct month.
         */
        while (showingDate.getMonth() == currentMonth) {
            showingDate.setDate(showingDate.getDate() - 1);
        }

        updateCalendar();
    }

    /**
     * Selects the previous year
     */
    private void selectPreviousYear(int years) {
        Date showingDate = datefield.getShowingDate();
        showingDate.setYear(showingDate.getYear() - years);
        updateCalendar();
    }

    /**
     * Selects the next year
     */
    private void selectNextYear(int years) {
        Date showingDate = datefield.getShowingDate();
        showingDate.setYear(showingDate.getYear() + years);
        updateCalendar();
    }

    /**
     * Handles a user click on the component
     * 
     * @param sender
     *            The component that was clicked
     * @param updateVariable
     *            Should the date field be updated
     * 
     */
    private void processClickEvent(Widget sender, boolean updateVariable) {
        if (!datefield.isEnabled() || datefield.isReadonly()) {
            return;
        }
        if (!updateVariable) {
            if (sender == prevYear) {
                selectPreviousYear(1);
            } else if (sender == nextYear) {
                selectNextYear(1);
            } else if (sender == prevMonth) {
                selectPreviousMonth();
            } else if (sender == nextMonth) {
                selectNextMonth();
            }
        } else {
            if (datefield.getCurrentResolution() == VDateField.RESOLUTION_YEAR
                    || datefield.getCurrentResolution() == VDateField.RESOLUTION_MONTH) {
                notifyServerOfChanges();
            }
        }
    }

    /**
     * Send the date to the server
     */
    private void notifyServerOfChanges() {
        Date showingDate = datefield.getShowingDate();

        // Due to current UI, update variable if res=year/month
        datefield.setCurrentDate(new Date(showingDate.getTime()));
        if (datefield.getCurrentResolution() == VDateField.RESOLUTION_MONTH) {
            datefield.getClient().updateVariable(datefield.getId(), "month",
                    datefield.getCurrentDate().getMonth() + 1, false);
        }
        datefield.getClient().updateVariable(datefield.getId(), "year",
                datefield.getCurrentDate().getYear() + 1900,
                datefield.isImmediate());

        /* Must update the value in the textfield also */
        updateCalendar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt
     * .user.client.ui.Widget, int, int)
     */
    public void onMouseDown(final Widget sender, int x, int y) {
        // Allow user to click-n-hold for fast-forward or fast-rewind.
        // Timer is first used for a 500ms delay after mousedown. After that has
        // elapsed, another timer is triggered to go off every 150ms. Both
        // timers are cancelled on mouseup or mouseout.
        if (sender instanceof VEventButton) {
            processClickEvent(sender, false);
            mouseTimer = new Timer() {
                @Override
                public void run() {
                    mouseTimer = new Timer() {
                        @Override
                        public void run() {
                            processClickEvent(sender, false);
                        }
                    };
                    mouseTimer.scheduleRepeating(150);
                }
            };
            mouseTimer.schedule(500);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt
     * .user.client.ui.Widget)
     */
    public void onMouseEnter(Widget sender) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt
     * .user.client.ui.Widget)
     */
    public void onMouseLeave(Widget sender) {
        if (mouseTimer != null) {
            mouseTimer.cancel();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt
     * .user.client.ui.Widget, int, int)
     */
    public void onMouseMove(Widget sender, int x, int y) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.
     * user.client.ui.Widget, int, int)
     */
    public void onMouseUp(Widget sender, int x, int y) {
        if (mouseTimer != null) {
            mouseTimer.cancel();
        }
        processClickEvent(sender, true);
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    public void onKeyDown(KeyDownEvent event) {
        int keycode = event.getNativeKeyCode();

        if (handleNavigation(keycode, event.isControlKeyDown()
                || event.isMetaKeyDown(), event.isShiftKeyDown())) {
            event.preventDefault();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google
     * .gwt.event.dom.client.KeyPressEvent)
     */
    public void onKeyPress(KeyPressEvent event) {
        if (handleNavigation(event.getNativeEvent().getKeyCode(), event
                .isControlKeyDown()
                || event.isMetaKeyDown(), event.isShiftKeyDown())) {
            event.preventDefault();
        }
    }

    /**
     * Get the first valid weekday column index of this month
     * 
     * @return A column index or zero if not found
     */
    private int getFirstWeekdayColumn() {
        Widget day = null;
        for (int col = 1; col <= 7; col++) {
            day = days.getWidget(1, col);
            if (day != null) {
                return col;
            }
        }
        return 0;
    }

    /**
     * Get the last valid weekday column index of this month
     * 
     * @return A column index or zero if not found
     */
    private int[] getLastWeekdayColumn() {
        Widget day = null;
        for (int row = days.getRowCount() - 1; row >= 1; row--) {
            for (int col = 7; col >= 1; col--) {
                day = days.getWidget(row, col);
                if (day != null) {
                    return new int[] { row, col };
                }
            }
        }
        return new int[] { 0, 0 };
    }

    /**
     * Handles the keyboard navigation when the resolution is set to years.
     * 
     * @param keycode
     *            The keycode to process
     * @param ctrl
     *            Is ctrl pressed?
     * @param shift
     *            is shift pressed
     * @return Returns true if the keycode was processed, else false
     */
    protected boolean handleNavigationYearMode(int keycode, boolean ctrl,
            boolean shift) {

        // Ctrl and Shift selection not supported
        if (ctrl || shift) {
            return false;
        }

        else if (keycode == getPreviousKey()) {
            selectNextYear(10); // Add 10 years
            return true;
        }

        else if (keycode == getForwardKey()) {
            selectNextYear(1); // Add 1 year
            return true;
        }

        else if (keycode == getNextKey()) {
            selectPreviousYear(10); // Subtract 10 years
            return true;
        }

        else if (keycode == getBackwardKey()) {
            selectPreviousYear(1); // Subtract 1 year
            return true;

        } else if (keycode == getSelectKey()) {

            // We need to notify the clickhandler of the selection
            dateClickHandler.selectDate(String.valueOf(datefield
                    .getShowingDate().getDay()));

            // Send changes to server
            notifyServerOfChanges();

            if (datefield instanceof VTextualDate) {
                ((VTextualDate) datefield).focus();
            }
            return true;

        } else if (keycode == getResetKey()) {
            // Restore showing date the selected date
            Date showing = datefield.getShowingDate();
            Date current = datefield.getCurrentDate();
            showing.setTime(current.getTime());
            updateCalendar();
            return true;

        } else if (keycode == getCloseKey()) {
            if (datefield instanceof VPopupCalendar) {

                // Restore showing date the selected date
                Date showing = datefield.getShowingDate();
                Date current = datefield.getCurrentDate();

                if (current != null && current != null) {
                    showing.setTime(current.getTime());
                }

                // Close popup..
                ((VPopupCalendar) datefield).closeCalendarPanel();

                // ..and focus the textfield
                ((VPopupCalendar) datefield).focus();
            }

            return true;
        }
        return false;
    }

    /**
     * Handle the keyboard navigation when the resolution is set to MONTH
     * 
     * @param keycode
     *            The keycode to handle
     * @param ctrl
     *            Was the ctrl key pressed?
     * @param shift
     *            Was the shift key pressed?
     * @return
     */
    protected boolean handleNavigationMonthMode(int keycode, boolean ctrl,
            boolean shift) {

        // Ctrl selection not supported
        if (ctrl) {
            return false;

        } else if (keycode == getPreviousKey()) {
            selectNextYear(1); // Add 1 year
            return true;

        } else if (keycode == getForwardKey()) {
            selectNextMonth(); // Add 1 month
            return true;

        } else if (keycode == getNextKey()) {
            selectPreviousYear(1); // Subtract 1 year
            return true;

        } else if (keycode == getBackwardKey()) {
            selectPreviousMonth(); // Subtract 1 month
            return true;

        } else if (keycode == getSelectKey()) {

            // We need to notify the clickhandler of the selection
            dateClickHandler.selectDate(String.valueOf(datefield
                    .getShowingDate().getDay()));

            // Send changes to server
            notifyServerOfChanges();

            if (datefield instanceof VTextualDate) {
                ((VTextualDate) datefield).focus();
            }

            return true;

        } else if (keycode == getResetKey()) {
            // Restore showing date the selected date
            Date showing = datefield.getShowingDate();
            Date current = datefield.getCurrentDate();
            showing.setTime(current.getTime());
            updateCalendar();
            return true;

        } else if (keycode == getCloseKey() || keycode == KeyCodes.KEY_TAB) {
            if (datefield instanceof VPopupCalendar) {

                // Restore showing date the selected date
                Date showing = datefield.getShowingDate();
                Date current = datefield.getCurrentDate();

                if (current != null && current != null) {
                    showing.setTime(current.getTime());
                }

                // Close popup..
                ((VPopupCalendar) datefield).closeCalendarPanel();

                // ..and focus the textfield
                ((VPopupCalendar) datefield).focus();
            }

            return true;
        }

        return false;
    }

    /**
     * Handle keyboard navigation what the resolution is set to DAY
     * 
     * @param keycode
     *            The keycode to handle
     * @param ctrl
     *            Was the ctrl key pressed?
     * @param shift
     *            Was the shift key pressed?
     * @return Return true if the key press was handled by the method, else
     *         return false.
     */
    protected boolean handleNavigationDayMode(int keycode, boolean ctrl,
            boolean shift) {

        // Ctrl key is not in use
        if (ctrl) {
            return false;
        }

        Date showingDate = datefield.getShowingDate();
        Widget currentSelection = days.getWidget(selectedRow, selectedColumn);

        /*
         * Jumps to the next day.
         */
        if (keycode == getForwardKey() && !shift) {
            // Calculate new showing date
            Date newCurrentDate = new Date(showingDate.getYear(), showingDate
                    .getMonth(), showingDate.getDate(), showingDate.getHours(),
                    showingDate.getMinutes(), showingDate.getSeconds());
            newCurrentDate.setDate(newCurrentDate.getDate() + 1);

            if (newCurrentDate.getMonth() == showingDate.getMonth()) {
                // Month did not change, only move the selection

                showingDate.setDate(showingDate.getDate() + 1);

                if (currentSelection != null) {
                    currentSelection.removeStyleDependentName("selected");

                    // Calculate new selection
                    if (selectedColumn == 7) {
                        selectedColumn = 1;
                        selectedRow++;
                    } else {
                        selectedColumn++;
                    }
                }

                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                currentSelection.addStyleDependentName("selected");

            } else {
                // If the month changed we need to re-render the calendar
                showingDate.setDate(showingDate.getDate() + 1);
                updateCalendarOnly();

                selectedRow = 1;
                selectedColumn = getFirstWeekdayColumn();

                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                currentSelection.addStyleDependentName("selected");
            }

            return true;

            /*
             * Jumps to the previous day
             */
        } else if (keycode == getBackwardKey() && !shift) {
            // Calculate new showing date
            Date newCurrentDate = new Date(showingDate.getYear(), showingDate
                    .getMonth(), showingDate.getDate(), showingDate.getHours(),
                    showingDate.getMinutes(), showingDate.getSeconds());
            newCurrentDate.setDate(newCurrentDate.getDate() - 1);

            if (newCurrentDate.getMonth() == showingDate.getMonth()) {
                // Month did not change, only move the selection

                showingDate.setDate(showingDate.getDate() - 1);

                if (currentSelection != null) {
                    currentSelection.removeStyleDependentName("selected");

                    // Calculate new selection
                    if (selectedColumn == 1) {
                        selectedColumn = 7;
                        selectedRow--;
                    } else {
                        selectedColumn--;
                    }

                }
                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                currentSelection.addStyleDependentName("selected");

            } else {
                // If the month changed we need to re-render the calendar
                showingDate.setDate(showingDate.getDate() - 1);
                updateCalendarOnly();

                int[] pos = getLastWeekdayColumn();
                selectedRow = pos[0];
                selectedColumn = pos[1];

                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                currentSelection.addStyleDependentName("selected");
            }

            return true;

            /*
             * Jumps one week back in the calendar
             */
        } else if (keycode == getPreviousKey() && !shift) {
            // Calculate new showing date
            Date newCurrentDate = new Date(showingDate.getYear(), showingDate
                    .getMonth(), showingDate.getDate(), showingDate.getHours(),
                    showingDate.getMinutes(), showingDate.getSeconds());
            newCurrentDate.setDate(newCurrentDate.getDate() - 7);

            if (newCurrentDate.getMonth() == showingDate.getMonth()
                    && selectedRow > 1) {
                // Month did not change, only move the selection

                showingDate.setDate(showingDate.getDate() - 7);

                if (currentSelection != null) {
                    currentSelection.removeStyleDependentName("selected");
                }

                selectedRow--;

                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                currentSelection.addStyleDependentName("selected");

            } else {
                // If the month changed we need to re-render the calendar
                showingDate.setDate(showingDate.getDate() - 7);
                updateCalendarOnly();

                int[] pos = getLastWeekdayColumn();
                selectedRow = pos[0];

                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                if (currentSelection == null) {
                    selectedRow--;
                    currentSelection = days.getWidget(selectedRow,
                            selectedColumn);
                }

                currentSelection.addStyleDependentName("selected");
            }

            return true;

            /*
             * Jumps one week forward in the calendar
             */
        } else if (keycode == getNextKey() && !ctrl && !shift) {
            // Calculate new showing date
            Date newCurrentDate = new Date(showingDate.getYear(), showingDate
                    .getMonth(), showingDate.getDate(), showingDate.getHours(),
                    showingDate.getMinutes(), showingDate.getSeconds());
            newCurrentDate.setDate(newCurrentDate.getDate() + 7);

            if (newCurrentDate.getMonth() == showingDate.getMonth()) {
                // Month did not change, only move the selection

                showingDate.setDate(showingDate.getDate() + 7);

                if (currentSelection != null) {
                    currentSelection.removeStyleDependentName("selected");
                }

                selectedRow++;

                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                currentSelection.addStyleDependentName("selected");

            } else {
                // If the month changed we need to re-render the calendar
                showingDate.setDate(showingDate.getDate() + 7);
                updateCalendarOnly();

                selectedRow = 1;

                // Set new selection
                currentSelection = days.getWidget(selectedRow, selectedColumn);
                if (currentSelection == null) {
                    selectedRow++;
                    currentSelection = days.getWidget(selectedRow,
                            selectedColumn);
                }

                currentSelection.addStyleDependentName("selected");
            }

            return true;

            /*
             * Selects the date that is chosen
             */
        } else if (keycode == getSelectKey() && !shift) {
            InlineHTML selection = (InlineHTML) days.getWidget(selectedRow,
                    selectedColumn);
            dateClickHandler.selectDate(selection.getText());

            if (datefield instanceof VTextualDate) {
                ((VTextualDate) datefield).focus();
            }

            return true;

            /*
             * Closes the date popup
             */
        } else if (keycode == getCloseKey() || keycode == KeyCodes.KEY_TAB) {
            if (datefield instanceof VPopupCalendar) {

                // Restore showing date the selected date
                Date showing = datefield.getShowingDate();
                Date current = datefield.getCurrentDate();

                if (current != null && current != null) {
                    showing.setTime(current.getTime());
                }

                // Close popup..
                ((VPopupCalendar) datefield).closeCalendarPanel();

                // ..and focus the textfield
                ((VPopupCalendar) datefield).focus();

            }

            return true;

            /*
             * Selects the next month
             */
        } else if (shift && keycode == getForwardKey()) {
            selectNextMonth();
            return true;

            /*
             * Selects the previous month
             */
        } else if (shift && keycode == getBackwardKey()) {
            selectPreviousMonth();
            return true;

            /*
             * Selects the next year
             */
        } else if (shift && keycode == getPreviousKey()) {
            selectNextYear(1);
            return true;

            /*
             * Selects the previous year
             */
        } else if (shift && keycode == getNextKey()) {
            selectPreviousYear(1);
            return true;

            /*
             * Resets the selection
             */
        } else if (keycode == getResetKey() && !shift) {
            // Restore showing date the selected date
            Date showing = datefield.getShowingDate();
            Date current = datefield.getCurrentDate();
            showing.setTime(current.getTime());
            updateCalendar();
            return true;
        }

        return false;
    }

    /**
     * Handles the keyboard navigation
     * 
     * @param keycode
     *            The key code that was pressed
     * @param ctrl
     *            Was the ctrl key pressed
     * @param shift
     *            Was the shift key pressed
     * @return Return true if key press was handled by the component, else
     *         return false
     */
    @SuppressWarnings("deprecation")
    protected boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
        if (!datefield.isEnabled() || datefield.isReadonly() || changingView) {
            return false;
        }

        else if (resolution == VDateField.RESOLUTION_YEAR) {
            return handleNavigationYearMode(keycode, ctrl, shift);
        }

        else if (resolution == VDateField.RESOLUTION_MONTH) {
            return handleNavigationMonthMode(keycode, ctrl, shift);
        }

        else if (resolution == VDateField.RESOLUTION_DAY) {
            return handleNavigationDayMode(keycode, ctrl, shift);
        }

        else {
            // Do not close the window on tab key but move the
            // focus down to the time
            if (keycode == KeyCodes.KEY_TAB) {
                time.setFocus(true);
                return true;
            }

            return handleNavigationDayMode(keycode, ctrl, shift);
        }

    }

    /**
     * Returns the reset key which will reset the calendar to the previous
     * selection. By default this is backspace but it can be overriden to change
     * the key to whatever you want.
     * 
     * @return
     */
    protected int getResetKey() {
        return KeyCodes.KEY_BACKSPACE;
    }

    /**
     * Returns the select key which selects the date. By default this is the
     * enter key but it can be changed to whatever you like by overriding this
     * method.
     * 
     * @return
     */
    protected int getSelectKey() {
        return KeyCodes.KEY_ENTER;
    }

    /**
     * Returns the key that closes the popup window if this is a VPopopCalendar.
     * Else this does nothing. By default this is the Escape key but you can
     * change the key to whatever you want by overriding this method.
     * 
     * @return
     */
    protected int getCloseKey() {
        return KeyCodes.KEY_ESCAPE;
    }

    /**
     * The key that selects the next day in the calendar. By default this is the
     * right arrow key but by overriding this method it can be changed to
     * whatever you like.
     * 
     * @return
     */
    protected int getForwardKey() {
        return KeyCodes.KEY_RIGHT;
    }

    /**
     * The key that selects the previous day in the calendar. By default this is
     * the left arrow key but by overriding this method it can be changed to
     * whatever you like.
     * 
     * @return
     */
    protected int getBackwardKey() {
        return KeyCodes.KEY_LEFT;
    }

    /**
     * The key that selects the next week in the calendar. By default this is
     * the down arrow key but by overriding this method it can be changed to
     * whatever you like.
     * 
     * @return
     */
    protected int getNextKey() {
        return KeyCodes.KEY_DOWN;
    }

    /**
     * The key that selects the previous week in the calendar. By default this
     * is the up arrow key but by overriding this method it can be changed to
     * whatever you like.
     * 
     * @return
     */
    protected int getPreviousKey() {
        return KeyCodes.KEY_UP;
    }

}
