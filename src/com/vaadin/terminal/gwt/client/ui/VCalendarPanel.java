/*
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.ui;

import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.DateTimeService;

@SuppressWarnings("deprecation")
public class VCalendarPanel extends FocusableFlexTable implements
        KeyDownHandler, KeyPressHandler, MouseOutHandler, MouseDownHandler,
        MouseUpHandler, BlurHandler, FocusHandler {

    public interface SubmitListener {

        /**
         * Called when calendar user triggers a submitting operation in calendar
         * panel. Eg. clicking on day or hitting enter.
         */
        void onSubmit();

        /**
         * On eg. ESC key.
         */
        void onCancel();
    }

    /**
     * Blur listener that listens to blur event from the panel
     */
    public interface FocusOutListener {
        /**
         * @return true if the calendar panel is not used after focus moves out
         */
        boolean onFocusOut(DomEvent event);
    }

    /**
     * Dispatches an event when the panel changes its _focused_ value.
     */
    public interface ValueChangeListener {
        /**
         * 
         * @return true if the calendar panel will not be used anymore
         */
        void changed(Date date);
    }

    /**
     * Dispatches an event when the panel when time is changed
     */
    public interface TimeChangeListener {

        void changed(int hour, int min, int sec, int msec);
    }

    /**
     * Represents a Date button in the calendar
     */
    private class VEventButton extends Button {
        public VEventButton() {
            addMouseDownHandler(VCalendarPanel.this);
            addMouseOutHandler(VCalendarPanel.this);
            addMouseUpHandler(VCalendarPanel.this);
        }
    }

    private static final String CN_FOCUSED = "focused";

    private static final String CN_TODAY = "today";

    private static final String CN_SELECTED = "selected";

    /**
     * Represents a click handler for when a user selects a value by using the
     * mouse
     */
    private ClickHandler dayClickHandler = new ClickHandler() {
        /*
         * (non-Javadoc)
         * 
         * @see
         * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt
         * .event.dom.client.ClickEvent)
         */
        public void onClick(ClickEvent event) {
            Day day = (Day) event.getSource();
            focusDay(day.getDay());
            selectFocused();
            onSubmit();
        }
    };

    private VEventButton prevYear;

    private VEventButton nextYear;

    private VEventButton prevMonth;

    private VEventButton nextMonth;

    private VTime time;

    private FlexTable days = new FlexTable();

    /* Needed to identify resolution changes */
    private int oldResolution = 0;
    private int resolution = VDateField.RESOLUTION_YEAR;

    private int focusedRow;
    private int focusedColumn;

    private Timer mouseTimer;

    private Date value;

    private boolean enabled = true;

    private boolean readonly = false;

    private DateTimeService dateTimeService;

    private boolean showISOWeekNumbers;

    private Date focusedDate;

    private Day selectedDay;

    private Day focusedDay;

    private FocusOutListener focusOutListener;

    private SubmitListener submitListener;

    private ValueChangeListener valueChangeListener;

    private TimeChangeListener timeChangeListener;

    private boolean hasFocus = false;

    public VCalendarPanel() {

        setStyleName(VDateField.CLASSNAME + "-calendarpanel");

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
        addFocusHandler(this);
        addBlurHandler(this);

    }

    /**
     * Sets the focus to given day of current time. Used when moving in the
     * calender with the keyboard.
     * 
     * @param day
     *            The day number from by Date.getDate()
     */
    private void focusDay(int day) {
        // Only used when calender body is present
        if (resolution > VDateField.RESOLUTION_MONTH) {
            if (focusedDay != null) {
                focusedDay.removeStyleDependentName(CN_FOCUSED);
            }

            if (day > 0 && focusedDate != null) {
                focusedDate.setDate(day);
                int rowCount = days.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    int cellCount = days.getCellCount(i);
                    for (int j = 0; j < cellCount; j++) {
                        Widget widget = days.getWidget(i, j);
                        if (widget != null && widget instanceof Day) {
                            Day curday = (Day) widget;
                            if (curday.getDay() == day) {
                                curday.addStyleDependentName(CN_FOCUSED);
                                focusedDay = curday;
                                focusedColumn = j;
                                focusedRow = i;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets the selection hightlight to a given date of current time
     * 
     * @param day
     */
    private void selectDate(int day) {

        value.setDate(day);

        if (selectedDay != null) {
            selectedDay.removeStyleDependentName(CN_SELECTED);
        }

        int rowCount = days.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            int cellCount = days.getCellCount(i);
            for (int j = 0; j < cellCount; j++) {
                Widget widget = days.getWidget(i, j);
                if (widget != null && widget instanceof Day) {
                    Day curday = (Day) widget;
                    if (curday.getDay() == day) {
                        curday.addStyleDependentName(CN_SELECTED);
                        selectedDay = curday;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Updates year, month, day from focusedDate to value
     */
    private void selectFocused() {
        if (focusedDate != null) {
            int changedFields = 0;
            if (value.getYear() != focusedDate.getYear()) {
                value.setYear(focusedDate.getYear());
                changedFields += VDateField.RESOLUTION_YEAR;
            }
            if (value.getMonth() != focusedDate.getMonth()) {
                value.setMonth(focusedDate.getMonth());
                changedFields += VDateField.RESOLUTION_MONTH;
            }
            if (value.getDate() != focusedDate.getDate()) {
                value.setDate(focusedDate.getDate());
                changedFields += VDateField.RESOLUTION_DAY;
            }

            selectDate(focusedDate.getDate());
        } else {
            ApplicationConnection.getConsole().log(
                    "Trying to select a the focused date which is NULL!");
        }
    }

    protected boolean onValueChange() {
        return false;
    }

    private int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        if (resolution != this.resolution) {
            oldResolution = this.resolution;
            this.resolution = resolution;
        }
    }

    private boolean isReadonly() {
        return readonly;
    }

    private boolean isEnabled() {
        return enabled;
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

    /**
     * Builds the top buttons and current month and year header.
     * 
     * @param forceRedraw
     *            Forces the builder to recreate the instances of the buttons.
     * @param needsMonth
     *            Should the month buttons be visible?
     */
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
                prevYear.setTabIndex(-1);
                nextYear = new VEventButton();
                nextYear.setHTML("&raquo;");
                nextYear.setStyleName("v-button-nextyear");
                nextYear.setTabIndex(-1);
                setWidget(0, 0, prevYear);
                setWidget(0, 4, nextYear);

                if (needsMonth) {
                    prevMonth = new VEventButton();
                    prevMonth.setHTML("&lsaquo;");
                    prevMonth.setStyleName("v-button-prevmonth");
                    prevMonth.setTabIndex(-1);
                    nextMonth = new VEventButton();
                    nextMonth.setHTML("&rsaquo;");
                    nextMonth.setStyleName("v-button-nextmonth");
                    nextMonth.setTabIndex(-1);
                    setWidget(0, 3, nextMonth);
                    setWidget(0, 1, prevMonth);
                }
            } else if (!needsMonth) {
                // Remove month traverse buttons
                remove(prevMonth);
                remove(nextMonth);
                prevMonth = null;
                nextMonth = null;
            }
        }

        final String monthName = needsMonth ? getDateTimeService().getMonth(
                focusedDate.getMonth()) : "";
        final int year = focusedDate.getYear() + 1900;
        getFlexCellFormatter().setStyleName(0, 2,
                VDateField.CLASSNAME + "-calendarpanel-month");
        setHTML(0, 2, "<span class=\"" + VDateField.CLASSNAME
                + "-calendarpanel-month\">" + monthName + " " + year
                + "</span>");
    }

    private DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Returns whether ISO 8601 week numbers should be shown in the value
     * selector or not. ISO 8601 defines that a week always starts with a Monday
     * so the week numbers are only shown if this is the case.
     * 
     * @return true if week number should be shown, false otherwise
     */
    public boolean isShowISOWeekNumbers() {
        return showISOWeekNumbers;
    }

    public void setShowISOWeekNumbers(boolean showISOWeekNumbers) {
        this.showISOWeekNumbers = showISOWeekNumbers;
    }

    /**
     * Builds the day and time selectors of the calendar.
     */
    private void buildCalendarBody() {

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
                isShowISOWeekNumbers());

        days.getRowFormatter().setStyleName(headerRow,
                VDateField.CLASSNAME + "-calendarpanel-weekdays");

        if (isShowISOWeekNumbers()) {
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
        final int firstDay = getDateTimeService().getFirstDayOfWeek();
        for (int i = 0; i < 7; i++) {
            int day = i + firstDay;
            if (day > 6) {
                day = 0;
            }
            if (getResolution() > VDateField.RESOLUTION_MONTH) {
                days.setHTML(headerRow, firstWeekdayColumn + i, "<strong>"
                        + getDateTimeService().getShortDay(day) + "</strong>");
            } else {
                days.setHTML(headerRow, firstWeekdayColumn + i, "");
            }
        }

        // The day of month that is selected, -1 if no day of this month is
        // selected (i.e, showing another month/year than selected or nothing is
        // selected)
        int dayOfMonthSelected = -1;
        // The day of month that is today, -1 if no day of this month is today
        // (i.e., showing another month/year than current)
        int dayOfMonthToday = -1;

        boolean initiallyNull = value == null;

        if (!initiallyNull && value.getMonth() == focusedDate.getMonth()
                && value.getYear() == focusedDate.getYear()) {
            dayOfMonthSelected = value.getDate();
        }
        final Date today = new Date();
        if (today.getMonth() == focusedDate.getMonth()
                && today.getYear() == focusedDate.getYear()) {
            dayOfMonthToday = today.getDate();
        }

        final int startWeekDay = getDateTimeService().getStartWeekDay(
                focusedDate);
        final int daysInMonth = DateTimeService
                .getNumberOfDaysInMonth(focusedDate);

        int dayCount = 0;
        final Date curr = new Date(focusedDate.getTime());

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

                    curr.setDate(dayCount);

                    // Actually write the day of month
                    Day day = new Day(dayOfMonth);

                    if (dayOfMonthSelected == dayOfMonth) {
                        day.addStyleDependentName(CN_SELECTED);
                        selectedDay = day;
                    }

                    if (dayOfMonthToday == dayOfMonth) {
                        day.addStyleDependentName(CN_TODAY);
                    }

                    if (dayOfMonth == focusedDate.getDate()) {
                        focusedDay = day;
                        focusedRow = weekOfMonth;
                        focusedColumn = firstWeekdayColumn + dayOfWeek;
                        if (hasFocus) {
                            day.addStyleDependentName(CN_FOCUSED);
                        }
                    }

                    days.setWidget(weekOfMonth, firstWeekdayColumn + dayOfWeek,
                            day);

                    // ISO week numbers if requested
                    if (!weekNumberProcessed[weekOfMonth]) {
                        days.getCellFormatter().setVisible(weekOfMonth,
                                weekColumn, isShowISOWeekNumbers());
                        if (isShowISOWeekNumbers()) {
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

    /**
     * Do we need the time selector
     * 
     * @return True if it is required
     */
    private boolean isTimeSelectorNeeded() {
        return getResolution() > VDateField.RESOLUTION_DAY;
    }

    /**
     * Updates the calendar and text field with the selected dates.
     */
    public void renderCalendar() {
        if (focusedDate == null) {
            focusedDate = new Date();
        }

        if (getResolution() <= VDateField.RESOLUTION_MONTH
                && valueChangeListener != null) {
            valueChangeListener.changed(new Date(focusedDate.getTime()));
        }

        Date start = new Date();
        final boolean needsMonth = getResolution() > VDateField.RESOLUTION_YEAR;
        boolean needsBody = getResolution() >= VDateField.RESOLUTION_DAY;
        buildCalendarHeader(true, needsMonth);
        clearCalendarBody(!needsBody);
        if (needsBody) {
            buildCalendarBody();
        }

        if (isTimeSelectorNeeded()
                && (time == null || resolution != oldResolution)) {
            time = new VTime();
            setWidget(2, 0, time);
            getFlexCellFormatter().setColSpan(2, 0, 5);
            getFlexCellFormatter().setStyleName(2, 0,
                    VDateField.CLASSNAME + "-calendarpanel-time");
            oldResolution = resolution;
        } else if (isTimeSelectorNeeded()) {
            time.updateTimes();
        } else if (time != null) {
            remove(time);
        }

        Date end = new Date();
        ApplicationConnection.getConsole().error(
                "Rendering calendar panel for(ms) "
                        + (end.getTime() - start.getTime()));

    }

    /**
     * Selects the next month
     */
    private void focusNextMonth() {

        int currentMonth = focusedDate.getMonth();
        focusedDate.setMonth(currentMonth + 1);
        int requestedMonth = (currentMonth + 1) % 12;

        /*
         * If the selected value was e.g. 31.3 the new value would be 31.4 but
         * this value is invalid so the new value will be 1.5. This is taken
         * care of by decreasing the value until we have the correct month.
         */
        while (focusedDate.getMonth() != requestedMonth) {
            focusedDate.setDate(focusedDate.getDate() - 1);
        }

        renderCalendar();
    }

    /**
     * Selects the previous month
     */
    private void focusPreviousMonth() {
        int currentMonth = focusedDate.getMonth();
        focusedDate.setMonth(currentMonth - 1);

        /*
         * If the selected value was e.g. 31.12 the new value would be 31.11 but
         * this value is invalid so the new value will be 1.12. This is taken
         * care of by decreasing the value until we have the correct month.
         */
        while (focusedDate.getMonth() == currentMonth) {
            focusedDate.setDate(focusedDate.getDate() - 1);
        }

        renderCalendar();
    }

    /**
     * Selects the previous year
     */
    private void focusPreviousYear(int years) {
        focusedDate.setYear(focusedDate.getYear() - years);
        renderCalendar();
    }

    /**
     * Selects the next year
     */
    private void focusNextYear(int years) {
        focusedDate.setYear(focusedDate.getYear() + years);
        renderCalendar();
    }

    /**
     * Handles a user click on the component
     * 
     * @param sender
     *            The component that was clicked
     * @param updateVariable
     *            Should the value field be updated
     * 
     */
    private void processClickEvent(Widget sender) {
        if (!isEnabled() || isReadonly()) {
            return;
        }
        if (sender == prevYear) {
            focusPreviousYear(1);
        } else if (sender == nextYear) {
            focusNextYear(1);
        } else if (sender == prevMonth) {
            focusPreviousMonth();
        } else if (sender == nextMonth) {
            focusNextMonth();
        }
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
        handleKeyPress(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google
     * .gwt.event.dom.client.KeyPressEvent)
     */
    public void onKeyPress(KeyPressEvent event) {
        handleKeyPress(event);
    }

    /**
     * Handles the keypress from both the onKeyPress event and the onKeyDown
     * event
     * 
     * @param event
     *            The keydown/keypress event
     */
    private void handleKeyPress(DomEvent event) {
        if (time != null
                && time.getElement().isOrHasChild(
                        (Node) event.getNativeEvent().getEventTarget().cast())) {
            int nativeKeyCode = event.getNativeEvent().getKeyCode();
            if (nativeKeyCode == getSelectKey()) {
                ApplicationConnection.getConsole().log(
                        "keydown on listselects"
                                + event.getNativeEvent().getKeyCode());
                onSubmit(); // submit happens if enter key hit down on listboxes
                event.preventDefault();
                event.stopPropagation();
            }
            return;
        }

        // Check tabs
        int keycode = event.getNativeEvent().getKeyCode();
        if (keycode == KeyCodes.KEY_TAB && event.getNativeEvent().getShiftKey()) {
            if (onTabOut(event)) {
                return;
            }
        }

        // Handle the navigation
        if (handleNavigation(keycode, event.getNativeEvent().getCtrlKey()
                || event.getNativeEvent().getMetaKey(), event.getNativeEvent()
                .getShiftKey())) {
            event.preventDefault();
        }

    }

    /**
     * Notifies submit-listeners of a submit event
     */
    private void onSubmit() {
        if (getSubmitListener() != null) {
            getSubmitListener().onSubmit();
        }
    }

    /**
     * Notifies submit-listeners of a cancel event
     */
    private void onCancel() {
        if (getSubmitListener() != null) {
            getSubmitListener().onCancel();
        }
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
            focusNextYear(10); // Add 10 years
            return true;
        }

        else if (keycode == getForwardKey()) {
            focusNextYear(1); // Add 1 year
            return true;
        }

        else if (keycode == getNextKey()) {
            focusPreviousYear(10); // Subtract 10 years
            return true;
        }

        else if (keycode == getBackwardKey()) {
            focusPreviousYear(1); // Subtract 1 year
            return true;

        } else if (keycode == getSelectKey()) {
            value = (Date) focusedDate.clone();
            onSubmit();
            return true;

        } else if (keycode == getResetKey()) {
            // Restore showing value the selected value
            focusedDate.setTime(value.getTime());
            renderCalendar();
            return true;

        } else if (keycode == getCloseKey()) {
            // TODO fire listener, on users responsibility??

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
            focusNextYear(1); // Add 1 year
            return true;

        } else if (keycode == getForwardKey()) {
            focusNextMonth(); // Add 1 month
            return true;

        } else if (keycode == getNextKey()) {
            focusPreviousYear(1); // Subtract 1 year
            return true;

        } else if (keycode == getBackwardKey()) {
            focusPreviousMonth(); // Subtract 1 month
            return true;

        } else if (keycode == getSelectKey()) {
            value = (Date) focusedDate.clone();
            onSubmit();
            return true;

        } else if (keycode == getResetKey()) {
            // Restore showing value the selected value
            focusedDate.setTime(value.getTime());
            renderCalendar();
            return true;

        } else if (keycode == getCloseKey() || keycode == KeyCodes.KEY_TAB) {

            // TODO fire close event

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

        /*
         * Jumps to the next day.
         */
        if (keycode == getForwardKey() && !shift) {
            // Calculate new showing value

            Date newCurrentDate = (Date) focusedDate.clone();

            newCurrentDate.setDate(newCurrentDate.getDate() + 1);

            if (newCurrentDate.getMonth() == focusedDate.getMonth()) {
                // Month did not change, only move the selection
                focusDay(focusedDate.getDate() + 1);
            } else {
                // If the month changed we need to re-render the calendar
                focusedDate.setDate(focusedDate.getDate() + 1);
                renderCalendar();
            }

            return true;

            /*
             * Jumps to the previous day
             */
        } else if (keycode == getBackwardKey() && !shift) {
            // Calculate new showing value
            Date newCurrentDate = (Date) focusedDate.clone();
            newCurrentDate.setDate(newCurrentDate.getDate() - 1);

            if (newCurrentDate.getMonth() == focusedDate.getMonth()) {
                // Month did not change, only move the selection
                focusDay(focusedDate.getDate() - 1);
            } else {
                // If the month changed we need to re-render the calendar
                focusedDate.setDate(focusedDate.getDate() - 1);
                renderCalendar();
            }

            return true;

            /*
             * Jumps one week back in the calendar
             */
        } else if (keycode == getPreviousKey() && !shift) {
            // Calculate new showing value
            Date newCurrentDate = (Date) focusedDate.clone();
            newCurrentDate.setDate(newCurrentDate.getDate() - 7);

            if (newCurrentDate.getMonth() == focusedDate.getMonth()
                    && focusedRow > 1) {
                // Month did not change, only move the selection
                focusDay(focusedDate.getDate() - 7);
            } else {
                // If the month changed we need to re-render the calendar
                focusedDate.setDate(focusedDate.getDate() - 7);
                renderCalendar();
            }

            return true;

            /*
             * Jumps one week forward in the calendar
             */
        } else if (keycode == getNextKey() && !ctrl && !shift) {
            // Calculate new showing value
            Date newCurrentDate = (Date) focusedDate.clone();
            newCurrentDate.setDate(newCurrentDate.getDate() + 7);

            if (newCurrentDate.getMonth() == focusedDate.getMonth()) {
                // Month did not change, only move the selection
                focusDay(focusedDate.getDate() + 7);
            } else {
                // If the month changed we need to re-render the calendar
                focusedDate.setDate(focusedDate.getDate() + 7);
                renderCalendar();

            }

            return true;

            /*
             * Selects the value that is chosen
             */
        } else if (keycode == getSelectKey() && !shift) {
            selectFocused();
            onSubmit(); // submit
            return true;
        } else if (keycode == getCloseKey()) {
            onCancel();
            // TODO close event

            return true;

            /*
             * Jumps to the next month
             */
        } else if (shift && keycode == getForwardKey()) {
            focusNextMonth();
            return true;

            /*
             * Jumps to the previous month
             */
        } else if (shift && keycode == getBackwardKey()) {
            focusPreviousMonth();
            return true;

            /*
             * Jumps to the next year
             */
        } else if (shift && keycode == getPreviousKey()) {
            focusNextYear(1);
            return true;

            /*
             * Jumps to the previous year
             */
        } else if (shift && keycode == getNextKey()) {
            focusPreviousYear(1);
            return true;

            /*
             * Resets the selection
             */
        } else if (keycode == getResetKey() && !shift) {
            // Restore showing value the selected value
            focusedDate.setTime(value.getTime());
            renderCalendar();
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
    protected boolean handleNavigation(int keycode, boolean ctrl, boolean shift) {
        if (!isEnabled() || isReadonly()) {
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
     * Returns the select key which selects the value. By default this is the
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

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google
     * .gwt.event.dom.client.MouseOutEvent)
     */
    public void onMouseOut(MouseOutEvent event) {
        if (mouseTimer != null) {
            mouseTimer.cancel();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google
     * .gwt.event.dom.client.MouseDownEvent)
     */
    public void onMouseDown(MouseDownEvent event) {
        // Allow user to click-n-hold for fast-forward or fast-rewind.
        // Timer is first used for a 500ms delay after mousedown. After that has
        // elapsed, another timer is triggered to go off every 150ms. Both
        // timers are cancelled on mouseup or mouseout.
        if (event.getSource() instanceof VEventButton) {
            final Widget sender = (Widget) event.getSource();
            processClickEvent(sender);
            mouseTimer = new Timer() {
                @Override
                public void run() {
                    mouseTimer = new Timer() {
                        @Override
                        public void run() {
                            processClickEvent(sender);
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
     * com.google.gwt.event.dom.client.MouseUpHandler#onMouseUp(com.google.gwt
     * .event.dom.client.MouseUpEvent)
     */
    public void onMouseUp(MouseUpEvent event) {
        if (mouseTimer != null) {
            mouseTimer.cancel();
        }
    }

    /**
     * Sets the data of the Panel.
     * 
     * @param currentDate
     *            The date to set
     */
    public void setDate(Date currentDate) {

        // Check that we are not re-rendering an already active date
        if (currentDate == value && currentDate != null) {
            return;
        }

        Date oldValue = value;
        value = currentDate;

        if (value == null) {
            focusedDate = null;
        } else {
            focusedDate = (Date) value.clone();
        }

        // Re-render calendar if the month or year has changed
        if (oldValue == null || value == null
                || oldValue.getYear() != value.getYear()
                || oldValue.getMonth() != value.getMonth()) {
            renderCalendar();
        } else {
            focusDay(currentDate.getDate());
            selectFocused();
        }

        if (!hasFocus) {
            focusDay(-1);
        }
    }

    /**
     * TimeSelector is a widget consisting of list boxes that modifie the Date
     * object that is given for.
     * 
     */
    public class VTime extends FlowPanel implements ChangeHandler {

        private ListBox hours;

        private ListBox mins;

        private ListBox sec;

        private ListBox msec;

        private ListBox ampm;

        private ListBox lastField;

        /**
         * Constructor
         */
        public VTime() {
            super();
            setStyleName(VDateField.CLASSNAME + "-time");
            buildTime();
        }

        private ListBox createListBox() {
            ListBox lb = new ListBox();
            lb.setStyleName(VNativeSelect.CLASSNAME);
            lb.addChangeHandler(this);
            lb.addBlurHandler(VCalendarPanel.this);
            lb.addFocusHandler(VCalendarPanel.this);
            return lb;
        }

        /**
         * Constructs the ListBoxes and updates their value
         * 
         * @param redraw
         *            Should new instances of the listboxes be created
         */
        private void buildTime() {
            clear();

            hours = createListBox();
            if (getDateTimeService().isTwelveHourClock()) {
                hours.addItem("12");
                for (int i = 1; i < 12; i++) {
                    hours.addItem((i < 10) ? "0" + i : "" + i);
                }
            } else {
                for (int i = 0; i < 24; i++) {
                    hours.addItem((i < 10) ? "0" + i : "" + i);
                }
            }

            hours.addChangeHandler(this);
            if (getDateTimeService().isTwelveHourClock()) {
                ampm = createListBox();
                final String[] ampmText = getDateTimeService().getAmPmStrings();
                ampm.addItem(ampmText[0]);
                ampm.addItem(ampmText[1]);
                ampm.addChangeHandler(this);
            }

            if (getResolution() >= VDateField.RESOLUTION_MIN) {
                mins = createListBox();
                for (int i = 0; i < 60; i++) {
                    mins.addItem((i < 10) ? "0" + i : "" + i);
                }
                mins.addChangeHandler(this);
            }
            if (getResolution() >= VDateField.RESOLUTION_SEC) {
                sec = createListBox();
                for (int i = 0; i < 60; i++) {
                    sec.addItem((i < 10) ? "0" + i : "" + i);
                }
                sec.addChangeHandler(this);
            }
            if (getResolution() == VDateField.RESOLUTION_MSEC) {
                msec = createListBox();
                for (int i = 0; i < 1000; i++) {
                    if (i < 10) {
                        msec.addItem("00" + i);
                    } else if (i < 100) {
                        msec.addItem("0" + i);
                    } else {
                        msec.addItem("" + i);
                    }
                }
                msec.addChangeHandler(this);
            }

            final String delimiter = getDateTimeService().getClockDelimeter();
            if (isReadonly()) {
                int h = 0;
                if (value != null) {
                    h = value.getHours();
                }
                if (getDateTimeService().isTwelveHourClock()) {
                    h -= h < 12 ? 0 : 12;
                }
                add(new VLabel(h < 10 ? "0" + h : "" + h));
            } else {
                add(hours);
                lastField = hours;
            }

            if (getResolution() >= VDateField.RESOLUTION_MIN) {
                add(new VLabel(delimiter));
                if (isReadonly()) {
                    final int m = mins.getSelectedIndex();
                    add(new VLabel(m < 10 ? "0" + m : "" + m));
                } else {
                    add(mins);
                    lastField = mins;
                }
            }
            if (getResolution() >= VDateField.RESOLUTION_SEC) {
                add(new VLabel(delimiter));
                if (isReadonly()) {
                    final int s = sec.getSelectedIndex();
                    add(new VLabel(s < 10 ? "0" + s : "" + s));
                } else {
                    add(sec);
                    lastField = sec;
                }
            }
            if (getResolution() == VDateField.RESOLUTION_MSEC) {
                add(new VLabel("."));
                if (isReadonly()) {
                    final int m = getMilliseconds();
                    final String ms = m < 100 ? "0" + m : "" + m;
                    add(new VLabel(m < 10 ? "0" + ms : ms));
                } else {
                    add(msec);
                    lastField = msec;
                }
            }
            if (getResolution() == VDateField.RESOLUTION_HOUR) {
                add(new VLabel(delimiter + "00")); // o'clock
            }
            if (getDateTimeService().isTwelveHourClock()) {
                add(new VLabel("&nbsp;"));
                if (isReadonly()) {
                    int i = 0;
                    if (value != null) {
                        i = (value.getHours() < 12) ? 0 : 1;
                    }
                    add(new VLabel(ampm.getItemText(i)));
                } else {
                    add(ampm);
                    lastField = ampm;
                }
            }

            if (isReadonly()) {
                return;
            }

            // Update times
            updateTimes();

            ListBox lastDropDown = (ListBox) getWidget(getWidgetCount() - 1);
            lastDropDown.addKeyDownHandler(new KeyDownHandler() {
                public void onKeyDown(KeyDownEvent event) {
                    boolean shiftKey = event.getNativeEvent().getShiftKey();
                    if (shiftKey) {
                        return;
                    } else {
                        int nativeKeyCode = event.getNativeKeyCode();
                        if (nativeKeyCode == KeyCodes.KEY_TAB) {
                            onTabOut(event);
                        }
                    }
                }
            });

        }

        /**
         * Updates the valus to correspond to the values in value
         */
        public void updateTimes() {
            boolean selected = true;
            if (value == null) {
                value = new Date();
                selected = false;
            }
            if (getDateTimeService().isTwelveHourClock()) {
                int h = value.getHours();
                ampm.setSelectedIndex(h < 12 ? 0 : 1);
                h -= ampm.getSelectedIndex() * 12;
                hours.setSelectedIndex(h);
            } else {
                hours.setSelectedIndex(value.getHours());
            }
            if (getResolution() >= VDateField.RESOLUTION_MIN) {
                mins.setSelectedIndex(value.getMinutes());
            }
            if (getResolution() >= VDateField.RESOLUTION_SEC) {
                sec.setSelectedIndex(value.getSeconds());
            }
            if (getResolution() == VDateField.RESOLUTION_MSEC) {
                if (selected) {
                    msec.setSelectedIndex(getMilliseconds());
                } else {
                    msec.setSelectedIndex(0);
                }
            }
            if (getDateTimeService().isTwelveHourClock()) {
                ampm.setSelectedIndex(value.getHours() < 12 ? 0 : 1);
            }

            hours.setEnabled(isEnabled());
            if (mins != null) {
                mins.setEnabled(isEnabled());
            }
            if (sec != null) {
                sec.setEnabled(isEnabled());
            }
            if (msec != null) {
                msec.setEnabled(isEnabled());
            }
            if (ampm != null) {
                ampm.setEnabled(isEnabled());
            }

        }

        private int getMilliseconds() {
            return DateTimeService.getMilliseconds(value);
        }

        private DateTimeService getDateTimeService() {
            if (dateTimeService == null) {
                dateTimeService = new DateTimeService();
            }
            return dateTimeService;
        }

        /*
         * (non-Javadoc) VT
         * 
         * @see
         * com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt
         * .event.dom.client.ChangeEvent)
         */
        public void onChange(ChangeEvent event) {
            /*
             * Value from dropdowns gets always set for the value. Like year and
             * month when resolution is month or year.
             */
            if (event.getSource() == hours) {
                int h = hours.getSelectedIndex();
                if (getDateTimeService().isTwelveHourClock()) {
                    h = h + ampm.getSelectedIndex() * 12;
                }
                value.setHours(h);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(h, value.getMinutes(),
                            value.getSeconds(),
                            DateTimeService.getMilliseconds(value));
                }
                event.preventDefault();
                event.stopPropagation();
            } else if (event.getSource() == mins) {
                final int m = mins.getSelectedIndex();
                value.setMinutes(m);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(value.getHours(), m,
                            value.getSeconds(),
                            DateTimeService.getMilliseconds(value));
                }
                event.preventDefault();
                event.stopPropagation();
            } else if (event.getSource() == sec) {
                final int s = sec.getSelectedIndex();
                value.setSeconds(s);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(value.getHours(),
                            value.getMinutes(), s,
                            DateTimeService.getMilliseconds(value));
                }
                event.preventDefault();
                event.stopPropagation();
            } else if (event.getSource() == msec) {
                final int ms = msec.getSelectedIndex();
                DateTimeService.setMilliseconds(value, ms);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(value.getHours(),
                            value.getMinutes(), value.getSeconds(), ms);
                }
                event.preventDefault();
                event.stopPropagation();
            } else if (event.getSource() == ampm) {
                final int h = hours.getSelectedIndex()
                        + (ampm.getSelectedIndex() * 12);
                value.setHours(h);
                if (timeChangeListener != null) {
                    timeChangeListener.changed(h, value.getMinutes(),
                            value.getSeconds(),
                            DateTimeService.getMilliseconds(value));
                }
                event.preventDefault();
                event.stopPropagation();
            }
        }

    }

    private class Day extends InlineHTML {
        private static final String BASECLASS = VDateField.CLASSNAME
                + "-calendarpanel-day";
        private final int day;

        Day(int dayOfMonth) {
            super("" + dayOfMonth);
            setStyleName(BASECLASS);
            day = dayOfMonth;
            addClickHandler(dayClickHandler);
        }

        public int getDay() {
            return day;
        }
    }

    public Date getDate() {
        return value;
    }

    /**
     * If true should be returned if the panel will not be used after this
     * event.
     * 
     * @param event
     * @return
     */
    protected boolean onTabOut(DomEvent event) {
        if (focusOutListener != null) {
            return focusOutListener.onFocusOut(event);
        }
        return false;
    }

    /**
     * A focus out listener is triggered when the panel loosed focus. This can
     * happen either after a user clicks outside the panel or tabs out.
     * 
     * @param listener
     *            The listener to trigger
     */
    public void setFocusOutListener(FocusOutListener listener) {
        focusOutListener = listener;
    }

    /**
     * The submit listener is called when the user selects a value from the
     * calender either by clicking the day or selects it by keyboard.
     * 
     * @param submitListener
     *            The listener to trigger
     */
    public void setSubmitListener(SubmitListener submitListener) {
        this.submitListener = submitListener;
    }

    /**
     * The value change listener is triggered when the focused date changes by
     * user either clicking on a new date or by using the keyboard.
     * 
     * @param listener
     *            The listener to trigger
     */
    public void setValueChangeListener(ValueChangeListener listener) {
        valueChangeListener = listener;
    }

    /**
     * The time change listener is triggered when the user changes the time.
     * 
     * @param listener
     */
    public void setTimeChangeListener(TimeChangeListener listener) {
        timeChangeListener = listener;
    }

    /**
     * Returns the submit listener that listens to selection made from the panel
     * 
     * @return The listener or NULL if no listener has been set
     */
    public SubmitListener getSubmitListener() {
        return submitListener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.BlurHandler#onBlur(com.google.gwt.event
     * .dom.client.BlurEvent)
     */
    public void onBlur(final BlurEvent event) {
        if (isAttached()) {
            DeferredCommand.addCommand(new Command() {
                public void execute() {
                    if (!hasFocus) {
                        onTabOut(event);
                    }
                }
            });
        }

        if (event.getSource() instanceof VCalendarPanel) {
            hasFocus = false;
            focusDay(-1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */
    public void onFocus(FocusEvent event) {
        if (event.getSource() instanceof VCalendarPanel) {
            hasFocus = true;

            // Focuses the current day if the calendar shows the days
            focusDay(focusedDay.getDay());
        }
    }

}
