/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client.ui;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gwt.aria.client.Roles;
import com.google.gwt.aria.client.SelectedValue;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.DateTimeService;
import com.vaadin.client.VConsole;
import com.vaadin.client.WidgetUtil;
import com.vaadin.shared.util.SharedUtil;

/**
 * Abstract calendar panel to show and select a date using a resolution. The
 * class is parameterized by the date resolution enumeration type.
 *
 * @author Vaadin Ltd
 *
 * @param <R>
 *            the resolution type which this field is based on (day, month, ...)
 * @since 8.0
 */
@SuppressWarnings("deprecation")
public abstract class VAbstractCalendarPanel<R extends Enum<R>>
        extends FocusableFlexTable implements KeyDownHandler, KeyPressHandler,
        MouseOutHandler, MouseDownHandler, MouseUpHandler, BlurHandler,
        FocusHandler, SubPartAware {

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
        boolean onFocusOut(DomEvent<?> event);
    }

    /**
     * FocusChangeListener is notified when the panel changes its _focused_
     * value.
     */
    public interface FocusChangeListener {
        void focusChanged(Date focusedDate);
    }

    /**
     * Represents a Date button in the calendar
     */
    private class VEventButton extends Button {
        public VEventButton() {
            addMouseDownHandler(VAbstractCalendarPanel.this);
            addMouseOutHandler(VAbstractCalendarPanel.this);
            addMouseUpHandler(VAbstractCalendarPanel.this);
        }
    }

    private static final String CN_FOCUSED = "focused";

    private static final String CN_TODAY = "today";

    private static final String CN_SELECTED = "selected";

    private static final String CN_OFFMONTH = "offmonth";

    private static final String CN_OUTSIDE_RANGE = "outside-range";

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
        @Override
        public void onClick(ClickEvent event) {
            if (!isEnabled() || isReadonly()) {
                return;
            }

            Date newDate = ((Day) event.getSource()).getDate();
            if (!isDateInsideRange(newDate,
                    getResolution(VAbstractCalendarPanel.this::isDay))) {
                return;
            }
            if (newDate.getMonth() != displayedMonth.getMonth()
                    || newDate.getYear() != displayedMonth.getYear()) {
                // If an off-month date was clicked, we must change the
                // displayed month and re-render the calendar (#8931)
                displayedMonth.setMonth(newDate.getMonth());
                displayedMonth.setYear(newDate.getYear());
                renderCalendar();
            }
            focusDay(newDate);
            selectFocused();
            onSubmit();
        }
    };

    private VEventButton prevYear;

    private VEventButton nextYear;

    private VEventButton prevMonth;

    private VEventButton nextMonth;

    private FlexTable days = new FlexTable();

    private R resolution;

    private Timer mouseTimer;

    private Date value;

    private DateTimeService dateTimeService;

    private boolean showISOWeekNumbers;

    private FocusedDate displayedMonth;

    private FocusedDate focusedDate;

    private Day selectedDay;

    private Day focusedDay;

    private FocusOutListener focusOutListener;

    private SubmitListener submitListener;

    private FocusChangeListener focusChangeListener;

    private boolean hasFocus = false;

    private VDateField<R> parent;

    private boolean initialRenderDone = false;

    public VAbstractCalendarPanel() {
        getElement().setId(DOM.createUniqueId());
        setStyleName(VDateField.CLASSNAME + "-calendarpanel");
        Roles.getGridRole().set(getElement());

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

    public void setParentField(VDateField<R> parent) {
        this.parent = parent;
    }

    /**
     * Sets the focus to given date in the current view. Used when moving in the
     * calendar with the keyboard.
     *
     * @param date
     *            A Date representing the day of month to be focused. Must be
     *            one of the days currently visible.
     */
    private void focusDay(Date date) {
        // Only used when calender body is present
        if (acceptDayFocus()) {
            if (focusedDay != null) {
                focusedDay.removeStyleDependentName(CN_FOCUSED);
            }

            if (date != null && focusedDate != null) {
                focusedDate.setTime(date.getTime());
                int rowCount = days.getRowCount();
                for (int i = 0; i < rowCount; i++) {
                    int cellCount = days.getCellCount(i);
                    for (int j = 0; j < cellCount; j++) {
                        Widget widget = days.getWidget(i, j);
                        if (widget != null
                                && widget instanceof VAbstractCalendarPanel.Day) {
                            Day curday = (Day) widget;
                            if (curday.getDate().equals(date)) {
                                curday.addStyleDependentName(CN_FOCUSED);
                                focusedDay = curday;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns {@code true} if current resolution assumes handling focus event
     * for day UI component.
     *
     * @return {@code true} if day focus events should be handled, {@code false}
     *         otherwise
     */
    protected abstract boolean acceptDayFocus();

    /**
     * Returns {@code true} if the provided {@code resolution} represents a day.
     *
     * @param resolution
     *            the given resolution
     * @return {@code true} if the {@code resolution} represents a day
     */
    protected abstract boolean isDay(R resolution);

    /**
     * Returns {@code true} if the provided {@code resolution} represents a
     * month.
     *
     * @param resolution
     *            the given resolution
     * @return {@code true} if the {@code resolution} represents a month
     */
    protected abstract boolean isMonth(R resolution);

    /**
     * Returns {@code true} if the provided {@code resolution} represents an
     * year.
     *
     * @param resolution
     *            the given resolution
     * @return {@code true} if the {@code resolution} represents a year
     */
    protected boolean isYear(R resolution) {
        return parent.isYear(resolution);
    }

    /**
     * Returns {@code true} if the {@code resolution} representation is strictly
     * below month (day, hour, etc..).
     *
     * @param resolution
     *            the given resolution
     * @return whether the {@code resolution} is below the month resolution
     */
    protected abstract boolean isBelowMonth(R resolution);

    /**
     * Returns all available resolutions for the widget.
     *
     * @return all available resolutions
     */
    protected Stream<R> getResolutions() {
        return parent.getResolutions();
    }

    /**
     * Finds the resolution by the {@code filter}.
     *
     * @param filter
     *            predicate to filter resolutions
     * @return the resolution accepted by the {@code filter}
     */
    protected R getResolution(Predicate<R> filter) {
        List<R> resolutions = getResolutions().filter(filter)
                .collect(Collectors.toList());
        assert resolutions
                .size() == 1 : "The result of filtering by the predicate "
                        + "contains unexpected number of resolution items :"
                        + resolutions.size();
        return resolutions.get(0);
    }

    /**
     * Sets the selection highlight to a given day in the current view
     *
     * @param date
     *            A Date representing the day of month to be selected. Must be
     *            one of the days currently visible.
     *
     */
    private void selectDate(Date date) {
        if (selectedDay != null) {
            selectedDay.removeStyleDependentName(CN_SELECTED);
            Roles.getGridcellRole()
                    .removeAriaSelectedState(selectedDay.getElement());
        }

        int rowCount = days.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            int cellCount = days.getCellCount(i);
            for (int j = 0; j < cellCount; j++) {
                Widget widget = days.getWidget(i, j);
                if (widget != null
                        && widget instanceof VAbstractCalendarPanel.Day) {
                    Day curday = (Day) widget;
                    if (curday.getDate().equals(date)) {
                        curday.addStyleDependentName(CN_SELECTED);
                        selectedDay = curday;
                        Roles.getGridcellRole().setAriaSelectedState(
                                selectedDay.getElement(), SelectedValue.TRUE);
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
        if (focusedDate != null
                && isDateInsideRange(focusedDate, getResolution())) {
            if (value == null) {
                // No previously selected value (set to null on server side).
                // Create a new date using current date and time
                value = new Date();
            }
            /*
             * #5594 set Date (day) to 1 in order to prevent any kind of
             * wrapping of months when later setting the month. (e.g. 31 ->
             * month with 30 days -> wraps to the 1st of the following month,
             * e.g. 31st of May -> 31st of April = 1st of May)
             */
            value.setDate(1);
            if (value.getYear() != focusedDate.getYear()) {
                value.setYear(focusedDate.getYear());
            }
            if (value.getMonth() != focusedDate.getMonth()) {
                value.setMonth(focusedDate.getMonth());
            }
            if (value.getDate() != focusedDate.getDate()) {
            }
            // We always need to set the date, even if it hasn't changed, since
            // it was forced to 1 above.
            value.setDate(focusedDate.getDate());

            selectDate(focusedDate);
        } else {
            VConsole.log("Trying to select a the focused date which is NULL!");
        }
    }

    protected boolean onValueChange() {
        return false;
    }

    public R getResolution() {
        return resolution;
    }

    public void setResolution(R resolution) {
        this.resolution = resolution;
    }

    /**
     * Checks whether the widget is not editable (read-only).
     *
     * @return {@code true} if the widget is read-only
     */
    protected boolean isReadonly() {
        return parent.isReadonly();
    }

    /**
     * Checks whether the widget is enabled.
     *
     * @return {@code true} is the widget is enabled
     */
    protected boolean isEnabled() {
        return parent.isEnabled();
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        if (initialRenderDone) {
            // Dynamic updates to the stylename needs to render the calendar to
            // update the inner element stylenames
            renderCalendar();
        }
    }

    @Override
    public void setStylePrimaryName(String style) {
        super.setStylePrimaryName(style);
        if (initialRenderDone) {
            // Dynamic updates to the stylename needs to render the calendar to
            // update the inner element stylenames
            renderCalendar();
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

    /**
     * Builds the top buttons and current month and year header.
     *
     * @param needsMonth
     *            Should the month buttons be visible?
     */
    private void buildCalendarHeader(boolean needsMonth) {

        getRowFormatter().addStyleName(0,
                parent.getStylePrimaryName() + "-calendarpanel-header");

        if (prevMonth == null && needsMonth) {
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
        } else if (prevMonth != null && !needsMonth) {
            // Remove month traverse buttons
            remove(prevMonth);
            remove(nextMonth);
            prevMonth = null;
            nextMonth = null;
        }

        if (prevYear == null) {

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
        }

        updateControlButtonRangeStyles(needsMonth);

        final String monthName = needsMonth
                ? getDateTimeService().getMonth(displayedMonth.getMonth()) : "";
        final int year = displayedMonth.getYear() + 1900;

        getFlexCellFormatter().setStyleName(0, 2,
                parent.getStylePrimaryName() + "-calendarpanel-month");
        getFlexCellFormatter().setStyleName(0, 0,
                parent.getStylePrimaryName() + "-calendarpanel-prevyear");
        getFlexCellFormatter().setStyleName(0, 4,
                parent.getStylePrimaryName() + "-calendarpanel-nextyear");
        getFlexCellFormatter().setStyleName(0, 3,
                parent.getStylePrimaryName() + "-calendarpanel-nextmonth");
        getFlexCellFormatter().setStyleName(0, 1,
                parent.getStylePrimaryName() + "-calendarpanel-prevmonth");

        setHTML(0, 2,
                "<span class=\"" + parent.getStylePrimaryName()
                        + "-calendarpanel-month\">" + monthName + " " + year
                        + "</span>");
    }

    private void updateControlButtonRangeStyles(boolean needsMonth) {

        if (focusedDate == null) {
            return;
        }

        if (needsMonth) {
            Date prevMonthDate = (Date) focusedDate.clone();
            removeOneMonth(prevMonthDate);

            R month = getResolution(VAbstractCalendarPanel.this::isMonth);
            if (!isDateInsideRange(prevMonthDate, month)) {
                prevMonth.addStyleName(CN_OUTSIDE_RANGE);
            } else {
                prevMonth.removeStyleName(CN_OUTSIDE_RANGE);
            }
            Date nextMonthDate = (Date) focusedDate.clone();
            addOneMonth(nextMonthDate);
            if (!isDateInsideRange(nextMonthDate, month)) {
                nextMonth.addStyleName(CN_OUTSIDE_RANGE);
            } else {
                nextMonth.removeStyleName(CN_OUTSIDE_RANGE);
            }
        }

        Date prevYearDate = (Date) focusedDate.clone();
        prevYearDate.setYear(prevYearDate.getYear() - 1);
        R year = getResolution(VAbstractCalendarPanel.this::isYear);
        if (!isDateInsideRange(prevYearDate, year)) {
            prevYear.addStyleName(CN_OUTSIDE_RANGE);
        } else {
            prevYear.removeStyleName(CN_OUTSIDE_RANGE);
        }

        Date nextYearDate = (Date) focusedDate.clone();
        nextYearDate.setYear(nextYearDate.getYear() + 1);
        if (!isDateInsideRange(nextYearDate, year)) {
            nextYear.addStyleName(CN_OUTSIDE_RANGE);
        } else {
            nextYear.removeStyleName(CN_OUTSIDE_RANGE);
        }

    }

    /**
     * Returns date time service for the widget.
     *
     * @see #setDateTimeService(DateTimeService)
     *
     * @return date time service
     */
    protected DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Returns the date field which this panel is attached to.
     *
     * @return the "parent" date field
     */
    protected VDateField<R> getDateField() {
        return parent;
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
        if (initialRenderDone && isBelowMonth(resolution)) {
            clearCalendarBody(false);
            buildCalendarBody();
        }
    }

    /**
     * Checks inclusively whether a date is inside a range of dates or not.
     *
     * @param date
     * @return
     */
    private boolean isDateInsideRange(Date date, R minResolution) {
        assert (date != null);

        return isAcceptedByRangeEnd(date, minResolution)
                && isAcceptedByRangeStart(date, minResolution);
    }

    /**
     * Accepts dates greater than or equal to rangeStart, depending on the
     * resolution. If the resolution is set to DAY, the range will compare on a
     * day-basis. If the resolution is set to YEAR, only years are compared. So
     * even if the range is set to one millisecond in next year, also next year
     * will be included.
     *
     * @param date
     * @param minResolution
     * @return
     */
    private boolean isAcceptedByRangeStart(Date date, R minResolution) {
        assert (date != null);

        // rangeStart == null means that we accept all values below rangeEnd
        if (rangeStart == null) {
            return true;
        }

        Date valueDuplicate = (Date) date.clone();
        Date rangeStartDuplicate = (Date) rangeStart.clone();

        if (isYear(minResolution)) {
            return valueDuplicate.getYear() >= rangeStartDuplicate.getYear();
        }
        if (isMonth(minResolution)) {
            valueDuplicate = clearDateBelowMonth(valueDuplicate);
            rangeStartDuplicate = clearDateBelowMonth(rangeStartDuplicate);
        } else {
            valueDuplicate = clearDateBelowDay(valueDuplicate);
            rangeStartDuplicate = clearDateBelowDay(rangeStartDuplicate);
        }

        return !rangeStartDuplicate.after(valueDuplicate);
    }

    /**
     * Accepts dates earlier than or equal to rangeStart, depending on the
     * resolution. If the resolution is set to DAY, the range will compare on a
     * day-basis. If the resolution is set to YEAR, only years are compared. So
     * even if the range is set to one millisecond in next year, also next year
     * will be included.
     *
     * @param date
     * @param minResolution
     * @return
     */
    private boolean isAcceptedByRangeEnd(Date date, R minResolution) {
        assert (date != null);

        // rangeEnd == null means that we accept all values above rangeStart
        if (rangeEnd == null) {
            return true;
        }

        Date valueDuplicate = (Date) date.clone();
        Date rangeEndDuplicate = (Date) rangeEnd.clone();

        if (isYear(minResolution)) {
            return valueDuplicate.getYear() <= rangeEndDuplicate.getYear();
        }
        if (isMonth(minResolution)) {
            valueDuplicate = clearDateBelowMonth(valueDuplicate);
            rangeEndDuplicate = clearDateBelowMonth(rangeEndDuplicate);
        } else {
            valueDuplicate = clearDateBelowDay(valueDuplicate);
            rangeEndDuplicate = clearDateBelowDay(rangeEndDuplicate);
        }

        return !rangeEndDuplicate.before(valueDuplicate);

    }

    private static Date clearDateBelowMonth(Date date) {
        date.setDate(1);
        return clearDateBelowDay(date);
    }

    private static Date clearDateBelowDay(Date date) {
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        // Clearing milliseconds
        long time = date.getTime() / 1000;
        date = new Date(time * 1000);
        return date;
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
                getDateField().getStylePrimaryName() + "-calendarpanel-body");

        days.getFlexCellFormatter().setStyleName(headerRow, weekColumn,
                "v-week");
        days.setHTML(headerRow, weekColumn, "<strong></strong>");
        // Hide the week column if week numbers are not to be displayed.
        days.getFlexCellFormatter().setVisible(headerRow, weekColumn,
                isShowISOWeekNumbers());

        days.getRowFormatter().setStyleName(headerRow,
                getDateField().getStylePrimaryName()
                        + "-calendarpanel-weekdays");

        if (isShowISOWeekNumbers()) {
            days.getFlexCellFormatter().setStyleName(headerRow, weekColumn,
                    "v-first");
            days.getFlexCellFormatter().setStyleName(headerRow,
                    firstWeekdayColumn, "");
            days.getRowFormatter().addStyleName(headerRow,
                    getDateField().getStylePrimaryName()
                            + "-calendarpanel-weeknumbers");
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
            if (isBelowMonth(getResolution())) {
                days.setHTML(headerRow, firstWeekdayColumn + i, "<strong>"
                        + getDateTimeService().getShortDay(day) + "</strong>");
            } else {
                days.setHTML(headerRow, firstWeekdayColumn + i, "");
            }

            Roles.getColumnheaderRole().set(days.getCellFormatter()
                    .getElement(headerRow, firstWeekdayColumn + i));
        }

        // Zero out hours, minutes, seconds, and milliseconds to compare dates
        // without time part
        final Date tmp = new Date();
        final Date today = new Date(tmp.getYear(), tmp.getMonth(),
                tmp.getDate());

        final Date selectedDate = value == null ? null
                : new Date(value.getYear(), value.getMonth(), value.getDate());

        final int startWeekDay = getDateTimeService()
                .getStartWeekDay(displayedMonth);
        final Date curr = (Date) displayedMonth.clone();
        // Start from the first day of the week that at least partially belongs
        // to the current month
        curr.setDate(1 - startWeekDay);

        // No month has more than 6 weeks so 6 is a safe maximum for rows.
        for (int weekOfMonth = 1; weekOfMonth < 7; weekOfMonth++) {
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {

                // Actually write the day of month
                Date dayDate = (Date) curr.clone();
                Day day = new Day(dayDate);

                day.setStyleName(getDateField().getStylePrimaryName()
                        + "-calendarpanel-day");

                if (!isDateInsideRange(dayDate, getResolution(this::isDay))) {
                    day.addStyleDependentName(CN_OUTSIDE_RANGE);
                }

                if (curr.equals(selectedDate)) {
                    day.addStyleDependentName(CN_SELECTED);
                    Roles.getGridcellRole().setAriaSelectedState(
                            day.getElement(), SelectedValue.TRUE);
                    selectedDay = day;
                }
                if (curr.equals(today)) {
                    day.addStyleDependentName(CN_TODAY);
                }
                if (curr.equals(focusedDate)) {
                    focusedDay = day;
                    if (hasFocus) {
                        day.addStyleDependentName(CN_FOCUSED);
                    }
                }
                if (curr.getMonth() != displayedMonth.getMonth()) {
                    day.addStyleDependentName(CN_OFFMONTH);
                }

                days.setWidget(weekOfMonth, firstWeekdayColumn + dayOfWeek,
                        day);
                Roles.getGridcellRole().set(days.getCellFormatter().getElement(
                        weekOfMonth, firstWeekdayColumn + dayOfWeek));

                // ISO week numbers if requested
                days.getCellFormatter().setVisible(weekOfMonth, weekColumn,
                        isShowISOWeekNumbers());

                if (isShowISOWeekNumbers()) {
                    final String baseCssClass = getDateField()
                            .getStylePrimaryName()
                            + "-calendarpanel-weeknumber";
                    String weekCssClass = baseCssClass;

                    int weekNumber = DateTimeService.getISOWeekNumber(curr);

                    days.setHTML(weekOfMonth, 0, "<span class=\"" + weekCssClass
                            + "\"" + ">" + weekNumber + "</span>");
                }
                curr.setDate(curr.getDate() + 1);
            }
        }
    }

    /**
     * Updates the calendar and text field with the selected dates.
     */
    public void renderCalendar() {
        renderCalendar(true);
    }

    /**
     * For internal use only. May be removed or replaced in the future.
     *
     * Updates the calendar and text field with the selected dates.
     *
     * @param updateDate
     *            The value false prevents setting the selected date of the
     *            calendar based on focusedDate. That can be used when only the
     *            resolution of the calendar is changed and no date has been
     *            selected.
     */
    public void renderCalendar(boolean updateDate) {
        doRenderCalendar(updateDate);

        initialRenderDone = true;
    }

    /**
     * Performs the rendering required by the {@link #renderCalendar(boolean)}.
     * Subclasses may override this method to provide a custom implementation
     * avoiding {@link #renderCalendar(boolean)} override. The latter method
     * contains a common logic which should not be overriden.
     *
     * @param updateDate
     *            The value false prevents setting the selected date of the
     *            calendar based on focusedDate. That can be used when only the
     *            resolution of the calendar is changed and no date has been
     *            selected.
     */
    protected void doRenderCalendar(boolean updateDate) {
        super.setStylePrimaryName(
                getDateField().getStylePrimaryName() + "-calendarpanel");

        if (focusedDate == null) {
            Date now = new Date();
            // focusedDate must have zero hours, mins, secs, millisecs
            focusedDate = new FocusedDate(now.getYear(), now.getMonth(),
                    now.getDate());
            displayedMonth = new FocusedDate(now.getYear(), now.getMonth(), 1);
        }

        if (updateDate && !isDay(getResolution())
                && focusChangeListener != null) {
            focusChangeListener.focusChanged(new Date(focusedDate.getTime()));
        }

        final boolean needsMonth = !isYear(getResolution());
        boolean needsBody = isBelowMonth(resolution);
        buildCalendarHeader(needsMonth);
        clearCalendarBody(!needsBody);
        if (needsBody) {
            buildCalendarBody();
        }
    }

    /**
     * Moves the focus forward the given number of days.
     */
    private void focusNextDay(int days) {
        if (focusedDate == null) {
            return;
        }

        Date focusCopy = ((Date) focusedDate.clone());
        focusCopy.setDate(focusedDate.getDate() + days);
        if (!isDateInsideRange(focusCopy, getResolution())) {
            // If not inside allowed range, then do not move anything
            return;
        }

        int oldMonth = focusedDate.getMonth();
        int oldYear = focusedDate.getYear();
        focusedDate.setDate(focusedDate.getDate() + days);

        if (focusedDate.getMonth() == oldMonth
                && focusedDate.getYear() == oldYear) {
            // Month did not change, only move the selection
            focusDay(focusedDate);
        } else {

            // If the month changed we need to re-render the calendar
            displayedMonth.setMonth(focusedDate.getMonth());
            displayedMonth.setYear(focusedDate.getYear());
            renderCalendar();
        }
    }

    /**
     * Moves the focus backward the given number of days.
     */
    private void focusPreviousDay(int days) {
        focusNextDay(-days);
    }

    /**
     * Selects the next month
     */
    private void focusNextMonth() {

        if (focusedDate == null) {
            return;
        }
        // Trying to request next month
        Date requestedNextMonthDate = (Date) focusedDate.clone();
        addOneMonth(requestedNextMonthDate);

        if (!isDateInsideRange(requestedNextMonthDate,
                getResolution(this::isMonth))) {
            return;
        }

        // Now also checking whether the day is inside the range or not. If not
        // inside,
        // correct it
        if (!isDateInsideRange(requestedNextMonthDate,
                getResolution(this::isDay))) {
            requestedNextMonthDate = adjustDateToFitInsideRange(
                    requestedNextMonthDate);
        }

        focusedDate.setTime(requestedNextMonthDate.getTime());
        displayedMonth.setMonth(displayedMonth.getMonth() + 1);

        renderCalendar();
    }

    private static void addOneMonth(Date date) {
        int currentMonth = date.getMonth();
        int requestedMonth = (currentMonth + 1) % 12;

        date.setMonth(date.getMonth() + 1);

        /*
         * If the selected value was e.g. 31.3 the new value would be 31.4 but
         * this value is invalid so the new value will be 1.5. This is taken
         * care of by decreasing the value until we have the correct month.
         */
        while (date.getMonth() != requestedMonth) {
            date.setDate(date.getDate() - 1);
        }
    }

    private static void removeOneMonth(Date date) {
        int currentMonth = date.getMonth();

        date.setMonth(date.getMonth() - 1);

        /*
         * If the selected value was e.g. 31.12 the new value would be 31.11 but
         * this value is invalid so the new value will be 1.12. This is taken
         * care of by decreasing the value until we have the correct month.
         */
        while (date.getMonth() == currentMonth) {
            date.setDate(date.getDate() - 1);
        }
    }

    /**
     * Selects the previous month
     */
    private void focusPreviousMonth() {

        if (focusedDate == null) {
            return;
        }
        Date requestedPreviousMonthDate = (Date) focusedDate.clone();
        removeOneMonth(requestedPreviousMonthDate);

        if (!isDateInsideRange(requestedPreviousMonthDate,
                getResolution(this::isMonth))) {
            return;
        }

        if (!isDateInsideRange(requestedPreviousMonthDate,
                getResolution(this::isDay))) {
            requestedPreviousMonthDate = adjustDateToFitInsideRange(
                    requestedPreviousMonthDate);
        }
        focusedDate.setTime(requestedPreviousMonthDate.getTime());
        displayedMonth.setMonth(displayedMonth.getMonth() - 1);

        renderCalendar();
    }

    /**
     * Selects the previous year
     */
    private void focusPreviousYear(int years) {

        if (focusedDate == null) {
            return;
        }
        Date previousYearDate = (Date) focusedDate.clone();
        previousYearDate.setYear(previousYearDate.getYear() - years);
        // Do not focus if not inside range
        if (!isDateInsideRange(previousYearDate, getResolution(this::isYear))) {
            return;
        }
        // If we remove one year, but have to roll back a bit, fit it
        // into the calendar. Also the months have to be changed
        if (!isDateInsideRange(previousYearDate, getResolution(this::isDay))) {
            previousYearDate = adjustDateToFitInsideRange(previousYearDate);

            focusedDate.setYear(previousYearDate.getYear());
            focusedDate.setMonth(previousYearDate.getMonth());
            focusedDate.setDate(previousYearDate.getDate());
            displayedMonth.setYear(previousYearDate.getYear());
            displayedMonth.setMonth(previousYearDate.getMonth());
        } else {

            int currentMonth = focusedDate.getMonth();
            focusedDate.setYear(focusedDate.getYear() - years);
            displayedMonth.setYear(displayedMonth.getYear() - years);
            /*
             * If the focused date was a leap day (Feb 29), the new date becomes
             * Mar 1 if the new year is not also a leap year. Set it to Feb 28
             * instead.
             */
            if (focusedDate.getMonth() != currentMonth) {
                focusedDate.setDate(0);
            }
        }

        renderCalendar();
    }

    /**
     * Selects the next year
     */
    private void focusNextYear(int years) {

        if (focusedDate == null) {
            return;
        }
        Date nextYearDate = (Date) focusedDate.clone();
        nextYearDate.setYear(nextYearDate.getYear() + years);
        // Do not focus if not inside range
        if (!isDateInsideRange(nextYearDate, getResolution(this::isYear))) {
            return;
        }
        // If we add one year, but have to roll back a bit, fit it
        // into the calendar. Also the months have to be changed
        if (!isDateInsideRange(nextYearDate, getResolution(this::isDay))) {
            nextYearDate = adjustDateToFitInsideRange(nextYearDate);

            focusedDate.setYear(nextYearDate.getYear());
            focusedDate.setMonth(nextYearDate.getMonth());
            focusedDate.setDate(nextYearDate.getDate());
            displayedMonth.setYear(nextYearDate.getYear());
            displayedMonth.setMonth(nextYearDate.getMonth());
        } else {

            int currentMonth = focusedDate.getMonth();
            focusedDate.setYear(focusedDate.getYear() + years);
            displayedMonth.setYear(displayedMonth.getYear() + years);
            /*
             * If the focused date was a leap day (Feb 29), the new date becomes
             * Mar 1 if the new year is not also a leap year. Set it to Feb 28
             * instead.
             */
            if (focusedDate.getMonth() != currentMonth) {
                focusedDate.setDate(0);
            }
        }

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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt
     * .event.dom.client.KeyDownEvent)
     */
    @Override
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
    @Override
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
    private void handleKeyPress(DomEvent<?> event) {
        // Check tabs
        int keycode = event.getNativeEvent().getKeyCode();
        if (keycode == KeyCodes.KEY_TAB
                && event.getNativeEvent().getShiftKey()) {
            if (onTabOut(event)) {
                return;
            }
        }

        // Handle the navigation
        if (handleNavigation(keycode,
                event.getNativeEvent().getCtrlKey()
                        || event.getNativeEvent().getMetaKey(),
                event.getNativeEvent().getShiftKey())) {
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

            onCancel();
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
            onCancel();

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
            focusNextDay(1);
            return true;

            /*
             * Jumps to the previous day
             */
        } else if (keycode == getBackwardKey() && !shift) {
            focusPreviousDay(1);
            return true;

            /*
             * Jumps one week forward in the calendar
             */
        } else if (keycode == getNextKey() && !shift) {
            focusNextDay(7);
            return true;

            /*
             * Jumps one week back in the calendar
             */
        } else if (keycode == getPreviousKey() && !shift) {
            focusPreviousDay(7);
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
            focusedDate = new FocusedDate(value.getYear(), value.getMonth(),
                    value.getDate());
            displayedMonth = new FocusedDate(value.getYear(), value.getMonth(),
                    1);
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
    protected boolean handleNavigation(int keycode, boolean ctrl,
            boolean shift) {
        if (!isEnabled() || isReadonly()) {
            return false;
        }

        else if (isYear(getResolution())) {
            return handleNavigationYearMode(keycode, ctrl, shift);
        }

        else if (isMonth(getResolution())) {
            return handleNavigationMonthMode(keycode, ctrl, shift);
        }

        else if (isDay(getResolution())) {
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
    @Override
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
    @Override
    public void onMouseDown(MouseDownEvent event) {
        // Click-n-hold the left mouse button for fast-forward or fast-rewind.
        // Timer is first used for a 500ms delay after mousedown. After that has
        // elapsed, another timer is triggered to go off every 150ms. Both
        // timers are cancelled on mouseup or mouseout.
        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT && event
                .getSource() instanceof VAbstractCalendarPanel.VEventButton) {
            final VEventButton sender = (VEventButton) event.getSource();
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
    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (mouseTimer != null) {
            mouseTimer.cancel();
        }
    }

    /**
     * Adjusts a date to fit inside the range, only if outside
     *
     * @param date
     */
    private Date adjustDateToFitInsideRange(Date date) {
        if (rangeStart != null && rangeStart.after(date)) {
            date = (Date) rangeStart.clone();
        } else if (rangeEnd != null && rangeEnd.before(date)) {
            date = (Date) rangeEnd.clone();
        }
        return date;
    }

    /**
     * Sets the data of the Panel.
     *
     * @param currentDate
     *            The date to set
     */
    public void setDate(Date currentDate) {
        doSetDate(currentDate, false, () -> {
        });
    }

    /**
     * The actual implementation of the logic which sets the data of the Panel.
     * The method {@link #setDate(Date)} just delegate a call to this method
     * providing additional config parameters.
     *
     * @param currentDate
     *            currentDate The date to set
     * @param needRerender
     *            if {@code true} then calendar will be rerendered regardless of
     *            internal logic, otherwise the decision will be made on the
     *            internal state inside the method
     * @param focusAction
     *            an additional action which will be executed in case
     *            rerendering is not required
     */
    protected void doSetDate(Date currentDate, boolean needRerender,
            Runnable focusAction) {
        // Check that we are not re-rendering an already active date
        if (currentDate == value && currentDate != null) {
            return;
        }
        boolean currentDateWasAdjusted = false;
        // Check that selected date is inside the allowed range
        if (currentDate != null
                && !isDateInsideRange(currentDate, getResolution())) {
            currentDate = adjustDateToFitInsideRange(currentDate);
            currentDateWasAdjusted = true;
        }

        Date oldDisplayedMonth = displayedMonth;
        value = currentDate;

        // If current date was adjusted, we will not select any date,
        // since that will look like a date is selected. Instead we
        // only focus on the adjusted value
        if (value == null || currentDateWasAdjusted) {
            // If ranges enabled, we may need to focus on a different view to
            // potentially not get stuck
            if (rangeStart != null || rangeEnd != null) {
                Date dateThatFitsInsideRange = adjustDateToFitInsideRange(
                        new Date());
                focusedDate = new FocusedDate(dateThatFitsInsideRange.getYear(),
                        dateThatFitsInsideRange.getMonth(),
                        dateThatFitsInsideRange.getDate());
                displayedMonth = new FocusedDate(
                        dateThatFitsInsideRange.getYear(),
                        dateThatFitsInsideRange.getMonth(), 1);
                // value was adjusted. Set selected to null to not cause
                // confusion, but this is only needed (and allowed) when we have
                // a day
                // resolution
                if (isDay(getResolution())) {
                    value = null;
                }
            } else {
                focusedDate = displayedMonth = null;
            }
        } else {
            focusedDate = new FocusedDate(value.getYear(), value.getMonth(),
                    value.getDate());
            displayedMonth = new FocusedDate(value.getYear(), value.getMonth(),
                    1);
        }

        // Re-render calendar if the displayed month is changed.
        if (needRerender || oldDisplayedMonth == null || value == null
                || oldDisplayedMonth.getYear() != value.getYear()
                || oldDisplayedMonth.getMonth() != value.getMonth()) {
            renderCalendar();
        } else {
            focusDay(focusedDate);
            selectFocused();
            focusAction.run();
        }

        if (!hasFocus) {
            focusDay(null);
        }
    }

    /**
     * A widget representing a single day in the calendar panel.
     */
    private class Day extends InlineHTML {
        private final Date date;

        Day(Date date) {
            super("" + date.getDate());
            this.date = date;
            addClickHandler(dayClickHandler);
        }

        public Date getDate() {
            return date;
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
    protected boolean onTabOut(DomEvent<?> event) {
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
     * The given FocusChangeListener is notified when the focused date changes
     * by user either clicking on a new date or by using the keyboard.
     *
     * @param listener
     *            The FocusChangeListener to be notified
     */
    public void setFocusChangeListener(FocusChangeListener listener) {
        focusChangeListener = listener;
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
    @Override
    public void onBlur(final BlurEvent event) {
        if (event.getSource() instanceof VAbstractCalendarPanel) {
            hasFocus = false;
            focusDay(null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.google.gwt.event.dom.client.FocusHandler#onFocus(com.google.gwt.event
     * .dom.client.FocusEvent)
     */
    @Override
    public void onFocus(FocusEvent event) {
        if (event.getSource() instanceof VAbstractCalendarPanel) {
            hasFocus = true;

            // Focuses the current day if the calendar shows the days
            if (focusedDay != null) {
                focusDay(focusedDate);
            }
        }
    }

    private static final String SUBPART_NEXT_MONTH = "nextmon";
    private static final String SUBPART_PREV_MONTH = "prevmon";

    private static final String SUBPART_NEXT_YEAR = "nexty";
    private static final String SUBPART_PREV_YEAR = "prevy";
    private static final String SUBPART_HOUR_SELECT = "h";
    private static final String SUBPART_MINUTE_SELECT = "m";
    private static final String SUBPART_SECS_SELECT = "s";
    private static final String SUBPART_AMPM_SELECT = "ampm";
    private static final String SUBPART_DAY = "day";
    private static final String SUBPART_MONTH_YEAR_HEADER = "header";

    private Date rangeStart;

    private Date rangeEnd;

    @Override
    public String getSubPartName(
            com.google.gwt.user.client.Element subElement) {
        if (contains(nextMonth, subElement)) {
            return SUBPART_NEXT_MONTH;
        } else if (contains(prevMonth, subElement)) {
            return SUBPART_PREV_MONTH;
        } else if (contains(nextYear, subElement)) {
            return SUBPART_NEXT_YEAR;
        } else if (contains(prevYear, subElement)) {
            return SUBPART_PREV_YEAR;
        } else if (contains(days, subElement)) {
            // Day, find out which dayOfMonth and use that as the identifier
            Day day = WidgetUtil.findWidget(subElement, Day.class);
            if (day != null) {
                Date date = day.getDate();
                int id = date.getDate();
                // Zero or negative ids map to days of the preceding month,
                // past-the-end-of-month ids to days of the following month
                if (date.getMonth() < displayedMonth.getMonth()) {
                    id -= DateTimeService.getNumberOfDaysInMonth(date);
                } else if (date.getMonth() > displayedMonth.getMonth()) {
                    id += DateTimeService
                            .getNumberOfDaysInMonth(displayedMonth);
                }
                return SUBPART_DAY + id;
            }
        } else if (getCellFormatter().getElement(0, 2)
                .isOrHasChild(subElement)) {
            return SUBPART_MONTH_YEAR_HEADER;
        }

        return null;
    }

    /**
     * Checks if subElement is inside the widget DOM hierarchy.
     *
     * @param w
     * @param subElement
     * @return true if {@code w} is a parent of subElement, false otherwise.
     */
    protected boolean contains(Widget w, Element subElement) {
        if (w == null || w.getElement() == null) {
            return false;
        }

        return w.getElement().isOrHasChild(subElement);
    }

    @Override
    public com.google.gwt.user.client.Element getSubPartElement(
            String subPart) {
        if (SUBPART_NEXT_MONTH.equals(subPart)) {
            return nextMonth.getElement();
        }
        if (SUBPART_PREV_MONTH.equals(subPart)) {
            return prevMonth.getElement();
        }
        if (SUBPART_NEXT_YEAR.equals(subPart)) {
            return nextYear.getElement();
        }
        if (SUBPART_PREV_YEAR.equals(subPart)) {
            return prevYear.getElement();
        }
        if (subPart.startsWith(SUBPART_DAY)) {
            // Zero or negative ids map to days in the preceding month,
            // past-the-end-of-month ids to days in the following month
            int dayOfMonth = Integer
                    .parseInt(subPart.substring(SUBPART_DAY.length()));
            Date date = new Date(displayedMonth.getYear(),
                    displayedMonth.getMonth(), dayOfMonth);
            Iterator<Widget> iter = days.iterator();
            while (iter.hasNext()) {
                Widget w = iter.next();
                if (w instanceof VAbstractCalendarPanel.Day) {
                    Day day = (Day) w;
                    if (day.getDate().equals(date)) {
                        return day.getElement();
                    }
                }
            }
        }

        if (SUBPART_MONTH_YEAR_HEADER.equals(subPart)) {
            return DOM.asOld(
                    (Element) getCellFormatter().getElement(0, 2).getChild(0));
        }
        return null;
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        if (mouseTimer != null) {
            mouseTimer.cancel();
        }
    }

    /**
     * Helper class to inform the screen reader that the user changed the
     * selected date. It sets the value of a field that is outside the view, and
     * is defined as a live area. That way the screen reader recognizes the
     * change and reads it to the user.
     */
    public class FocusedDate extends Date {

        public FocusedDate(int year, int month, int date) {
            super(year, month, date);
        }

        @Override
        public void setTime(long time) {
            super.setTime(time);
            setLabel();
        }

        @Override
        @Deprecated
        public void setDate(int date) {
            super.setDate(date);
            setLabel();
        }

        @Override
        @Deprecated
        public void setMonth(int month) {
            super.setMonth(month);
            setLabel();
        }

        @Override
        @Deprecated
        public void setYear(int year) {
            super.setYear(year);
            setLabel();
        }

        private void setLabel() {
            if (getDateField() instanceof VAbstractPopupCalendar) {
                ((VAbstractPopupCalendar) getDateField()).setFocusedDate(this);
            }
        }
    }

    /**
     * Sets the start range for this component. The start range is inclusive,
     * and it depends on the current resolution, what is considered inside the
     * range.
     *
     * @param startDate
     *            - the allowed range's start date
     */
    public void setRangeStart(Date newRangeStart) {
        if (!SharedUtil.equals(rangeStart, newRangeStart)) {
            rangeStart = newRangeStart;
            if (initialRenderDone) {
                // Dynamic updates to the range needs to render the calendar to
                // update the element stylenames
                renderCalendar();
            }
        }

    }

    /**
     * Sets the end range for this component. The end range is inclusive, and it
     * depends on the current resolution, what is considered inside the range.
     *
     * @param endDate
     *            - the allowed range's end date
     */
    public void setRangeEnd(Date newRangeEnd) {
        if (!SharedUtil.equals(rangeEnd, newRangeEnd)) {
            rangeEnd = newRangeEnd;
            if (initialRenderDone) {
                // Dynamic updates to the range needs to render the calendar to
                // update the element stylenames
                renderCalendar();
            }
        }
    }
}
