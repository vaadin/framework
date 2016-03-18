/*
 * Copyright 2000-2014 Vaadin Ltd.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.calendar.schedule.CalendarDay;
import com.vaadin.client.ui.calendar.schedule.CalendarEvent;
import com.vaadin.client.ui.calendar.schedule.DayToolbar;
import com.vaadin.client.ui.calendar.schedule.MonthGrid;
import com.vaadin.client.ui.calendar.schedule.SimpleDayCell;
import com.vaadin.client.ui.calendar.schedule.SimpleDayToolbar;
import com.vaadin.client.ui.calendar.schedule.SimpleWeekToolbar;
import com.vaadin.client.ui.calendar.schedule.WeekGrid;
import com.vaadin.client.ui.calendar.schedule.WeeklyLongEvents;
import com.vaadin.client.ui.calendar.schedule.dd.CalendarDropHandler;
import com.vaadin.client.ui.dd.VHasDropHandler;
import com.vaadin.shared.ui.calendar.DateConstants;

/**
 * Client side implementation for Calendar
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 */
public class VCalendar extends Composite implements VHasDropHandler {

    public static final String ATTR_FIRSTDAYOFWEEK = "firstDay";
    public static final String ATTR_LASTDAYOFWEEK = "lastDay";
    public static final String ATTR_FIRSTHOUROFDAY = "firstHour";
    public static final String ATTR_LASTHOUROFDAY = "lastHour";

    // private boolean hideWeekends;
    private String[] monthNames;
    private String[] dayNames;
    private boolean format;
    private final DockPanel outer = new DockPanel();
    private int rows;

    private boolean rangeSelectAllowed = true;
    private boolean rangeMoveAllowed = true;
    private boolean eventResizeAllowed = true;
    private boolean eventMoveAllowed = true;

    private final SimpleDayToolbar nameToolbar = new SimpleDayToolbar();

    private final DayToolbar dayToolbar = new DayToolbar(this);
    private final SimpleWeekToolbar weekToolbar;
    private WeeklyLongEvents weeklyLongEvents;
    private MonthGrid monthGrid;
    private WeekGrid weekGrid;
    private int intWidth = 0;
    private int intHeight = 0;

    protected final DateTimeFormat dateformat_datetime = DateTimeFormat
            .getFormat("yyyy-MM-dd HH:mm:ss");
    protected final DateTimeFormat dateformat_date = DateTimeFormat
            .getFormat("yyyy-MM-dd");
    protected final DateTimeFormat time12format_date = DateTimeFormat
            .getFormat("h:mm a");
    protected final DateTimeFormat time24format_date = DateTimeFormat
            .getFormat("HH:mm");

    private boolean readOnly = false;
    private boolean disabled = false;

    private boolean isHeightUndefined = false;

    private boolean isWidthUndefined = false;
    private int firstDay;
    private int lastDay;
    private int firstHour;
    private int lastHour;

    private CalendarDropHandler dropHandler;

    /**
     * Listener interface for listening to event click events
     */
    public interface DateClickListener {
        /**
         * Triggered when a date was clicked
         * 
         * @param date
         *            The date and time that was clicked
         */
        void dateClick(String date);
    }

    /**
     * Listener interface for listening to week number click events
     */
    public interface WeekClickListener {
        /**
         * Called when a week number was selected.
         * 
         * @param event
         *            The format of the vent string is "<year>w<week>"
         */
        void weekClick(String event);
    }

    /**
     * Listener interface for listening to forward events
     */
    public interface ForwardListener {

        /**
         * Called when the calendar should move one view forward
         */
        void forward();
    }

    /**
     * Listener interface for listening to backward events
     */
    public interface BackwardListener {

        /**
         * Called when the calendar should move one view backward
         */
        void backward();
    }

    /**
     * Listener interface for listening to selection events
     */
    public interface RangeSelectListener {

        /**
         * Called when a user selected a new event by highlighting an area of
         * the calendar.
         * 
         * FIXME Fix the value nonsense.
         * 
         * @param value
         *            The format of the value string is
         *            "<year>:<start-minutes>:<end-minutes>" if called from the
         *            {@link SimpleWeekToolbar} and "<yyyy-MM-dd>TO<yyyy-MM-dd>"
         *            if called from {@link MonthGrid}
         */
        void rangeSelected(String value);
    }

    /**
     * Listener interface for listening to click events
     */
    public interface EventClickListener {
        /**
         * Called when an event was clicked
         * 
         * @param event
         *            The event that was clicked
         */
        void eventClick(CalendarEvent event);
    }

    /**
     * Listener interface for listening to event moved events. Occurs when a
     * user drags an event to a new position
     */
    public interface EventMovedListener {
        /**
         * Triggered when an event was dragged to a new position and the start
         * and end dates was changed
         * 
         * @param event
         *            The event that was moved
         */
        void eventMoved(CalendarEvent event);
    }

    /**
     * Listener interface for when an event gets resized (its start or end date
     * changes)
     */
    public interface EventResizeListener {
        /**
         * Triggers when the time limits for the event was changed.
         * 
         * @param event
         *            The event that was changed. The new time limits have been
         *            updated in the event before calling this method
         */
        void eventResized(CalendarEvent event);
    }

    /**
     * Listener interface for listening to scroll events.
     */
    public interface ScrollListener {
        /**
         * Triggered when the calendar is scrolled
         * 
         * @param scrollPosition
         *            The scroll position in pixels as returned by
         *            {@link ScrollPanel#getScrollPosition()}
         */
        void scroll(int scrollPosition);
    }

    /**
     * Listener interface for listening to mouse events.
     */
    public interface MouseEventListener {
        /**
         * Triggered when a user wants an context menu
         * 
         * @param event
         *            The context menu event
         * 
         * @param widget
         *            The widget that the context menu should be added to
         */
        void contextMenu(ContextMenuEvent event, Widget widget);
    }

    /**
     * Default constructor
     */
    public VCalendar() {
        weekToolbar = new SimpleWeekToolbar(this);
        initWidget(outer);
        setStylePrimaryName("v-calendar");
        blockSelect(getElement());
    }

    /**
     * Hack for IE to not select text when dragging.
     * 
     * @param e
     *            The element to apply the hack on
     */
    private native void blockSelect(Element e)
    /*-{
    	e.onselectstart = function() {
    		return false;
    	}

    	e.ondragstart = function() {
    		return false;
    	}
    }-*/;

    private void updateEventsToWeekGrid(CalendarEvent[] events) {
        List<CalendarEvent> allDayLong = new ArrayList<CalendarEvent>();
        List<CalendarEvent> belowDayLong = new ArrayList<CalendarEvent>();

        for (CalendarEvent e : events) {
            if (e.isAllDay()) {
                // Event is set on one "allDay" event or more than one.
                allDayLong.add(e);

            } else {
                // Event is set only on one day.
                belowDayLong.add(e);
            }
        }

        weeklyLongEvents.addEvents(allDayLong);

        for (CalendarEvent e : belowDayLong) {
            weekGrid.addEvent(e);
        }
    }

    /**
     * Adds events to the month grid
     * 
     * @param events
     *            The events to add
     * @param drawImmediately
     *            Should the grid be rendered immediately. (currently not in
     *            use)
     * 
     */
    public void updateEventsToMonthGrid(Collection<CalendarEvent> events,
            boolean drawImmediately) {
        for (CalendarEvent e : sortEventsByDuration(events)) {
            // FIXME Why is drawImmediately not used ?????
            addEventToMonthGrid(e, false);
        }
    }

    private void addEventToMonthGrid(CalendarEvent e, boolean renderImmediately) {
        Date when = e.getStart();
        Date to = e.getEnd();
        boolean eventAdded = false;
        boolean inProgress = false; // Event adding has started
        boolean eventMoving = false;
        List<SimpleDayCell> dayCells = new ArrayList<SimpleDayCell>();
        List<SimpleDayCell> timeCells = new ArrayList<SimpleDayCell>();
        for (int row = 0; row < monthGrid.getRowCount(); row++) {
            if (eventAdded) {
                break;
            }
            for (int cell = 0; cell < monthGrid.getCellCount(row); cell++) {
                SimpleDayCell sdc = (SimpleDayCell) monthGrid.getWidget(row,
                        cell);
                if (isEventInDay(when, to, sdc.getDate())
                        && isEventInDayWithTime(when, to, sdc.getDate(),
                                e.getEndTime(), e.isAllDay())) {
                    if (!eventMoving) {
                        eventMoving = sdc.getMoveEvent() != null;
                    }
                    long d = e.getRangeInMilliseconds();
                    if ((d > 0 && d <= DateConstants.DAYINMILLIS)
                            && !e.isAllDay()) {
                        timeCells.add(sdc);
                    } else {
                        dayCells.add(sdc);
                    }
                    inProgress = true;
                    continue;
                } else if (inProgress) {
                    eventAdded = true;
                    inProgress = false;
                    break;
                }
            }
        }

        updateEventSlotIndex(e, dayCells);
        updateEventSlotIndex(e, timeCells);

        for (SimpleDayCell sdc : dayCells) {
            sdc.addCalendarEvent(e);
        }
        for (SimpleDayCell sdc : timeCells) {
            sdc.addCalendarEvent(e);
        }

        if (renderImmediately) {
            reDrawAllMonthEvents(!eventMoving);
        }
    }

    /*
     * We must also handle the special case when the event lasts exactly for 24
     * hours, thus spanning two days e.g. from 1.1.2001 00:00 to 2.1.2001 00:00.
     * That special case still should span one day when rendered.
     */
    @SuppressWarnings("deprecation")
    // Date methods are not deprecated in GWT
    private boolean isEventInDayWithTime(Date from, Date to, Date date,
            Date endTime, boolean isAllDay) {
        return (isAllDay || !(to.getDay() == date.getDay()
                && from.getDay() != to.getDay() && isMidnight(endTime)));
    }

    private void updateEventSlotIndex(CalendarEvent e, List<SimpleDayCell> cells) {
        if (cells.isEmpty()) {
            return;
        }

        if (e.getSlotIndex() == -1) {
            // Update slot index
            int newSlot = -1;
            for (SimpleDayCell sdc : cells) {
                int slot = sdc.getEventCount();
                if (slot > newSlot) {
                    newSlot = slot;
                }
            }
            newSlot++;

            for (int i = 0; i < newSlot; i++) {
                // check for empty slot
                if (isSlotEmpty(e, i, cells)) {
                    newSlot = i;
                    break;
                }
            }
            e.setSlotIndex(newSlot);
        }
    }

    private void reDrawAllMonthEvents(boolean clearCells) {
        for (int row = 0; row < monthGrid.getRowCount(); row++) {
            for (int cell = 0; cell < monthGrid.getCellCount(row); cell++) {
                SimpleDayCell sdc = (SimpleDayCell) monthGrid.getWidget(row,
                        cell);
                sdc.reDraw(clearCells);
            }
        }
    }

    private boolean isSlotEmpty(CalendarEvent addedEvent, int slotIndex,
            List<SimpleDayCell> cells) {
        for (SimpleDayCell sdc : cells) {
            CalendarEvent e = sdc.getCalendarEvent(slotIndex);
            if (e != null && !e.equals(addedEvent)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove a month event from the view
     * 
     * @param target
     *            The event to remove
     * 
     * @param repaintImmediately
     *            Should we repaint after the event was removed?
     */
    public void removeMonthEvent(CalendarEvent target,
            boolean repaintImmediately) {
        if (target != null && target.getSlotIndex() >= 0) {
            // Remove event
            for (int row = 0; row < monthGrid.getRowCount(); row++) {
                for (int cell = 0; cell < monthGrid.getCellCount(row); cell++) {
                    SimpleDayCell sdc = (SimpleDayCell) monthGrid.getWidget(
                            row, cell);
                    if (sdc == null) {
                        return;
                    }
                    sdc.removeEvent(target, repaintImmediately);
                }
            }
        }
    }

    /**
     * Updates an event in the month grid
     * 
     * @param changedEvent
     *            The event that has changed
     */
    public void updateEventToMonthGrid(CalendarEvent changedEvent) {
        removeMonthEvent(changedEvent, true);
        changedEvent.setSlotIndex(-1);
        addEventToMonthGrid(changedEvent, true);
    }

    /**
     * Sort the event by how long they are
     * 
     * @param events
     *            The events to sort
     * @return An array where the events has been sorted
     */
    public CalendarEvent[] sortEventsByDuration(Collection<CalendarEvent> events) {
        CalendarEvent[] sorted = events
                .toArray(new CalendarEvent[events.size()]);
        Arrays.sort(sorted, getEventComparator());
        return sorted;
    }

    /*
     * Check if the given event occurs at the given date.
     */
    private boolean isEventInDay(Date eventWhen, Date eventTo, Date gridDate) {
        if (eventWhen.compareTo(gridDate) <= 0
                && eventTo.compareTo(gridDate) >= 0) {

            return true;
        }

        return false;
    }

    /**
     * Re-render the week grid
     * 
     * @param daysCount
     *            The amount of days to include in the week
     * @param days
     *            The days
     * @param today
     *            Todays date
     * @param realDayNames
     *            The names of the dates
     */
    @SuppressWarnings("deprecation")
    public void updateWeekGrid(int daysCount, List<CalendarDay> days,
            Date today, String[] realDayNames) {
        weekGrid.setFirstHour(getFirstHourOfTheDay());
        weekGrid.setLastHour(getLastHourOfTheDay());
        weekGrid.getTimeBar().updateTimeBar(is24HFormat());

        dayToolbar.clear();
        dayToolbar.addBackButton();
        dayToolbar.setVerticalSized(isHeightUndefined);
        dayToolbar.setHorizontalSized(isWidthUndefined);
        weekGrid.clearDates();
        weekGrid.setDisabled(isDisabledOrReadOnly());

        for (CalendarDay day : days) {
            String date = day.getDate();
            String localized_date_format = day.getLocalizedDateFormat();
            Date d = dateformat_date.parse(date);
            int dayOfWeek = day.getDayOfWeek();
            if (dayOfWeek < getFirstDayNumber()
                    || dayOfWeek > getLastDayNumber()) {
                continue;
            }
            boolean isToday = false;
            int dayOfMonth = d.getDate();
            if (today.getDate() == dayOfMonth && today.getYear() == d.getYear()
                    && today.getMonth() == d.getMonth()) {
                isToday = true;
            }
            dayToolbar.add(realDayNames[dayOfWeek - 1], date,
                    localized_date_format, isToday ? "today" : null);
            weeklyLongEvents.addDate(d);
            weekGrid.addDate(d);
            if (isToday) {
                weekGrid.setToday(d, today);
            }
        }
        dayToolbar.addNextButton();
    }

    /**
     * Updates the events in the Month view
     * 
     * @param daysCount
     *            How many days there are
     * @param daysUidl
     * 
     * @param today
     *            Todays date
     */
    @SuppressWarnings("deprecation")
    public void updateMonthGrid(int daysCount, List<CalendarDay> days,
            Date today) {
        int columns = getLastDayNumber() - getFirstDayNumber() + 1;
        rows = (int) Math.ceil(daysCount / (double) 7);

        monthGrid = new MonthGrid(this, rows, columns);
        monthGrid.setEnabled(!isDisabledOrReadOnly());
        weekToolbar.removeAllRows();
        int pos = 0;
        boolean monthNameDrawn = true;
        boolean firstDayFound = false;
        boolean lastDayFound = false;

        for (CalendarDay day : days) {
            String date = day.getDate();
            Date d = dateformat_date.parse(date);
            int dayOfWeek = day.getDayOfWeek();
            int week = day.getWeek();

            int dayOfMonth = d.getDate();

            // reset at start of each month
            if (dayOfMonth == 1) {
                monthNameDrawn = false;
                if (firstDayFound) {
                    lastDayFound = true;
                }
                firstDayFound = true;
            }

            if (dayOfWeek < getFirstDayNumber()
                    || dayOfWeek > getLastDayNumber()) {
                continue;
            }
            int y = (pos / columns);
            int x = pos - (y * columns);
            if (x == 0 && daysCount > 7) {
                // Add week to weekToolbar for navigation
                weekToolbar.addWeek(week, day.getYearOfWeek());
            }
            final SimpleDayCell cell = new SimpleDayCell(this, y, x);
            cell.setMonthGrid(monthGrid);
            cell.setDate(d);
            cell.addDomHandler(new ContextMenuHandler() {
                @Override
                public void onContextMenu(ContextMenuEvent event) {
                    if (mouseEventListener != null) {
                        event.preventDefault();
                        event.stopPropagation();
                        mouseEventListener.contextMenu(event, cell);
                    }
                }
            }, ContextMenuEvent.getType());

            if (!firstDayFound) {
                cell.addStyleDependentName("prev-month");
            } else if (lastDayFound) {
                cell.addStyleDependentName("next-month");
            }

            if (dayOfMonth >= 1 && !monthNameDrawn) {
                cell.setMonthNameVisible(true);
                monthNameDrawn = true;
            }

            if (today.getDate() == dayOfMonth && today.getYear() == d.getYear()
                    && today.getMonth() == d.getMonth()) {
                cell.setToday(true);

            }
            monthGrid.setWidget(y, x, cell);
            pos++;
        }
    }

    public void setSizeForChildren(int newWidth, int newHeight) {
        intWidth = newWidth;
        intHeight = newHeight;
        isWidthUndefined = intWidth == -1;
        dayToolbar.setVerticalSized(isHeightUndefined);
        dayToolbar.setHorizontalSized(isWidthUndefined);
        recalculateWidths();
        recalculateHeights();
    }

    /**
     * Recalculates the heights of the sub-components in the calendar
     */
    protected void recalculateHeights() {
        if (monthGrid != null) {

            if (intHeight == -1) {
                monthGrid.addStyleDependentName("sizedheight");
            } else {
                monthGrid.removeStyleDependentName("sizedheight");
            }

            monthGrid.updateCellSizes(intWidth - weekToolbar.getOffsetWidth(),
                    intHeight - nameToolbar.getOffsetHeight());
            weekToolbar.setHeightPX((intHeight == -1) ? intHeight : intHeight
                    - nameToolbar.getOffsetHeight());

        } else if (weekGrid != null) {
            weekGrid.setHeightPX((intHeight == -1) ? intHeight : intHeight
                    - weeklyLongEvents.getOffsetHeight()
                    - dayToolbar.getOffsetHeight());
        }
    }

    /**
     * Recalculates the widths of the sub-components in the calendar
     */
    protected void recalculateWidths() {
        if (!isWidthUndefined) {
            nameToolbar.setWidthPX(intWidth);
            dayToolbar.setWidthPX(intWidth);

            if (monthGrid != null) {
                monthGrid.updateCellSizes(
                        intWidth - weekToolbar.getOffsetWidth(), intHeight
                                - nameToolbar.getOffsetHeight());
            } else if (weekGrid != null) {
                weekGrid.setWidthPX(intWidth);
                weeklyLongEvents.setWidthPX(weekGrid.getInternalWidth());
            }
        } else {
            dayToolbar.setWidthPX(intWidth);
            nameToolbar.setWidthPX(intWidth);

            if (monthGrid != null) {
                if (intWidth == -1) {
                    monthGrid.addStyleDependentName("sizedwidth");

                } else {
                    monthGrid.removeStyleDependentName("sizedwidth");
                }
            } else if (weekGrid != null) {
                weekGrid.setWidthPX(intWidth);
                weeklyLongEvents.setWidthPX(weekGrid.getInternalWidth());
            }
        }
    }

    /**
     * Get the date format used to format dates only (excludes time)
     * 
     * @return
     */
    public DateTimeFormat getDateFormat() {
        return dateformat_date;
    }

    /**
     * Get the time format used to format time only (excludes date)
     * 
     * @return
     */
    public DateTimeFormat getTimeFormat() {
        if (is24HFormat()) {
            return time24format_date;
        }
        return time12format_date;
    }

    /**
     * Get the date and time format to format the dates (includes both date and
     * time)
     * 
     * @return
     */
    public DateTimeFormat getDateTimeFormat() {
        return dateformat_datetime;
    }

    /**
     * Is the calendar either disabled or readonly
     * 
     * @return
     */
    public boolean isDisabledOrReadOnly() {
        return disabled || readOnly;
    }

    /**
     * Is the component disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Is the component disabled
     * 
     * @param disabled
     *            True if disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Is the component read-only
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Is the component read-only
     * 
     * @param readOnly
     *            True if component is readonly
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Get the month grid component
     * 
     * @return
     */
    public MonthGrid getMonthGrid() {
        return monthGrid;
    }

    /**
     * Get he week grid component
     * 
     * @return
     */
    public WeekGrid getWeekGrid() {
        return weekGrid;
    }

    /**
     * Calculates correct size for all cells (size / amount of cells ) and
     * distributes any overflow over all the cells.
     * 
     * @param totalSize
     *            the total amount of size reserved for all cells
     * @param numberOfCells
     *            the number of cells
     * @param sizeModifier
     *            a modifier which is applied to all cells before distributing
     *            the overflow
     * @return an integer array that contains the correct size for each cell
     */
    public static int[] distributeSize(int totalSize, int numberOfCells,
            int sizeModifier) {
        int[] cellSizes = new int[numberOfCells];
        int startingSize = totalSize / numberOfCells;
        int cellSizeOverFlow = totalSize % numberOfCells;

        for (int i = 0; i < numberOfCells; i++) {
            cellSizes[i] = startingSize + sizeModifier;
        }

        // distribute size overflow amongst all slots
        int j = 0;
        while (cellSizeOverFlow > 0) {
            cellSizes[j]++;
            cellSizeOverFlow--;
            j++;
            if (j >= numberOfCells) {
                j = 0;
            }
        }

        // cellSizes[numberOfCells - 1] += cellSizeOverFlow;

        return cellSizes;
    }

    /**
     * Returns a comparator which can compare calendar events.
     * 
     * @return
     */
    public static Comparator<CalendarEvent> getEventComparator() {
        return new Comparator<CalendarEvent>() {

            @Override
            public int compare(CalendarEvent o1, CalendarEvent o2) {
                if (o1.isAllDay() != o2.isAllDay()) {
                    if (o2.isAllDay()) {
                        return 1;
                    }
                    return -1;
                }

                Long d1 = o1.getRangeInMilliseconds();
                Long d2 = o2.getRangeInMilliseconds();
                int r = 0;
                if (!d1.equals(0L) && !d2.equals(0L)) {
                    r = d2.compareTo(d1);
                    return (r == 0) ? ((Integer) o2.getIndex()).compareTo(o1
                            .getIndex()) : r;
                }

                if (d2.equals(0L) && d1.equals(0L)) {
                    return ((Integer) o2.getIndex()).compareTo(o1.getIndex());
                } else if (d2.equals(0L) && d1 >= DateConstants.DAYINMILLIS) {
                    return -1;
                } else if (d2.equals(0L) && d1 < DateConstants.DAYINMILLIS) {
                    return 1;
                } else if (d1.equals(0L) && d2 >= DateConstants.DAYINMILLIS) {
                    return 1;
                } else if (d1.equals(0L) && d2 < DateConstants.DAYINMILLIS) {
                    return -1;
                }
                r = d2.compareTo(d1);
                return (r == 0) ? ((Integer) o2.getIndex()).compareTo(o1
                        .getIndex()) : r;
            }
        };
    }

    /**
     * Is the date at midnight
     * 
     * @param date
     *            The date to check
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isMidnight(Date date) {
        return (date.getHours() == 0 && date.getMinutes() == 0 && date
                .getSeconds() == 0);
    }

    /**
     * Are the dates equal (uses second resolution)
     * 
     * @param date1
     *            The first the to compare
     * @param date2
     *            The second date to compare
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean areDatesEqualToSecond(Date date1, Date date2) {
        return date1.getYear() == date2.getYear()
                && date1.getMonth() == date2.getMonth()
                && date1.getDay() == date2.getDay()
                && date1.getHours() == date2.getHours()
                && date1.getSeconds() == date2.getSeconds();
    }

    /**
     * Is the calendar event zero seconds long and is occurring at midnight
     * 
     * @param event
     *            The event to check
     * @return
     */
    public static boolean isZeroLengthMidnightEvent(CalendarEvent event) {
        return areDatesEqualToSecond(event.getStartTime(), event.getEndTime())
                && isMidnight(event.getEndTime());
    }

    /**
     * Should the 24h time format be used
     * 
     * @param format
     *            True if the 24h format should be used else the 12h format is
     *            used
     */
    public void set24HFormat(boolean format) {
        this.format = format;
    }

    /**
     * Is the 24h time format used
     */
    public boolean is24HFormat() {
        return format;
    }

    /**
     * Set the names of the week days
     * 
     * @param names
     *            The names of the days (Monday, Thursday,...)
     */
    public void setDayNames(String[] names) {
        assert (names.length == 7);
        dayNames = names;
    }

    /**
     * Get the names of the week days
     */
    public String[] getDayNames() {
        return dayNames;
    }

    /**
     * Set the names of the months
     * 
     * @param names
     *            The names of the months (January, February,...)
     */
    public void setMonthNames(String[] names) {
        assert (names.length == 12);
        monthNames = names;
    }

    /**
     * Get the month names
     */
    public String[] getMonthNames() {
        return monthNames;
    }

    /**
     * Set the number when a week starts
     * 
     * @param dayNumber
     *            The number of the day
     */
    public void setFirstDayNumber(int dayNumber) {
        assert (dayNumber >= 1 && dayNumber <= 7);
        firstDay = dayNumber;
    }

    /**
     * Get the number when a week starts
     */
    public int getFirstDayNumber() {
        return firstDay;
    }

    /**
     * Set the number when a week ends
     * 
     * @param dayNumber
     *            The number of the day
     */
    public void setLastDayNumber(int dayNumber) {
        assert (dayNumber >= 1 && dayNumber <= 7);
        lastDay = dayNumber;
    }

    /**
     * Get the number when a week ends
     */
    public int getLastDayNumber() {
        return lastDay;
    }

    /**
     * Set the number when a week starts
     * 
     * @param dayNumber
     *            The number of the day
     */
    public void setFirstHourOfTheDay(int hour) {
        assert (hour >= 0 && hour <= 23);
        firstHour = hour;
    }

    /**
     * Get the number when a week starts
     */
    public int getFirstHourOfTheDay() {
        return firstHour;
    }

    /**
     * Set the number when a week ends
     * 
     * @param dayNumber
     *            The number of the day
     */
    public void setLastHourOfTheDay(int hour) {
        assert (hour >= 0 && hour <= 23);
        lastHour = hour;
    }

    /**
     * Get the number when a week ends
     */
    public int getLastHourOfTheDay() {
        return lastHour;
    }

    /**
     * Re-renders the whole week view
     * 
     * @param scroll
     *            The amount of pixels to scroll the week view
     * @param today
     *            Todays date
     * @param daysInMonth
     *            How many days are there in the month
     * @param firstDayOfWeek
     *            The first day of the week
     * @param events
     *            The events to render
     */
    public void updateWeekView(int scroll, Date today, int daysInMonth,
            int firstDayOfWeek, Collection<CalendarEvent> events,
            List<CalendarDay> days) {

        while (outer.getWidgetCount() > 0) {
            outer.remove(0);
        }

        monthGrid = null;
        String[] realDayNames = new String[getDayNames().length];
        int j = 0;

        if (firstDayOfWeek == 2) {
            for (int i = 1; i < getDayNames().length; i++) {
                realDayNames[j++] = getDayNames()[i];
            }
            realDayNames[j] = getDayNames()[0];
        } else {
            for (int i = 0; i < getDayNames().length; i++) {
                realDayNames[j++] = getDayNames()[i];
            }

        }

        weeklyLongEvents = new WeeklyLongEvents(this);
        if (weekGrid == null) {
            weekGrid = new WeekGrid(this, is24HFormat());
        }
        updateWeekGrid(daysInMonth, days, today, realDayNames);
        updateEventsToWeekGrid(sortEventsByDuration(events));
        outer.add(dayToolbar, DockPanel.NORTH);
        outer.add(weeklyLongEvents, DockPanel.NORTH);
        outer.add(weekGrid, DockPanel.SOUTH);
        weekGrid.setVerticalScrollPosition(scroll);
    }

    /**
     * Re-renders the whole month view
     * 
     * @param firstDayOfWeek
     *            The first day of the week
     * @param today
     *            Todays date
     * @param daysInMonth
     *            Amount of days in the month
     * @param events
     *            The events to render
     * @param days
     *            The day information
     */
    public void updateMonthView(int firstDayOfWeek, Date today,
            int daysInMonth, Collection<CalendarEvent> events,
            List<CalendarDay> days) {

        // Remove all week numbers from bar
        while (outer.getWidgetCount() > 0) {
            outer.remove(0);
        }

        int firstDay = getFirstDayNumber();
        int lastDay = getLastDayNumber();
        int daysPerWeek = lastDay - firstDay + 1;
        int j = 0;

        String[] dayNames = getDayNames();
        String[] realDayNames = new String[daysPerWeek];

        if (firstDayOfWeek == 2) {
            for (int i = firstDay; i < lastDay + 1; i++) {
                if (i == 7) {
                    realDayNames[j++] = dayNames[0];
                } else {
                    realDayNames[j++] = dayNames[i];
                }
            }
        } else {
            for (int i = firstDay - 1; i < lastDay; i++) {
                realDayNames[j++] = dayNames[i];
            }
        }

        nameToolbar.setDayNames(realDayNames);

        weeklyLongEvents = null;
        weekGrid = null;

        updateMonthGrid(daysInMonth, days, today);

        outer.add(nameToolbar, DockPanel.NORTH);
        outer.add(weekToolbar, DockPanel.WEST);
        weekToolbar.updateCellHeights();
        outer.add(monthGrid, DockPanel.CENTER);

        updateEventsToMonthGrid(events, false);
    }

    private DateClickListener dateClickListener;

    /**
     * Sets the listener for listening to event clicks
     * 
     * @param listener
     *            The listener to use
     */
    public void setListener(DateClickListener listener) {
        dateClickListener = listener;
    }

    /**
     * Gets the listener for listening to event clicks
     * 
     * @return
     */
    public DateClickListener getDateClickListener() {
        return dateClickListener;
    }

    private ForwardListener forwardListener;

    /**
     * Set the listener which listens to forward events from the calendar
     * 
     * @param listener
     *            The listener to use
     */
    public void setListener(ForwardListener listener) {
        forwardListener = listener;
    }

    /**
     * Get the listener which listens to forward events from the calendar
     * 
     * @return
     */
    public ForwardListener getForwardListener() {
        return forwardListener;
    }

    private BackwardListener backwardListener;

    /**
     * Set the listener which listens to backward events from the calendar
     * 
     * @param listener
     *            The listener to use
     */
    public void setListener(BackwardListener listener) {
        backwardListener = listener;
    }

    /**
     * Set the listener which listens to backward events from the calendar
     * 
     * @return
     */
    public BackwardListener getBackwardListener() {
        return backwardListener;
    }

    private WeekClickListener weekClickListener;

    /**
     * Set the listener that listens to user clicking on the week numbers
     * 
     * @param listener
     *            The listener to use
     */
    public void setListener(WeekClickListener listener) {
        weekClickListener = listener;
    }

    /**
     * Get the listener that listens to user clicking on the week numbers
     * 
     * @return
     */
    public WeekClickListener getWeekClickListener() {
        return weekClickListener;
    }

    private RangeSelectListener rangeSelectListener;

    /**
     * Set the listener that listens to the user highlighting a region in the
     * calendar
     * 
     * @param listener
     *            The listener to use
     */
    public void setListener(RangeSelectListener listener) {
        rangeSelectListener = listener;
    }

    /**
     * Get the listener that listens to the user highlighting a region in the
     * calendar
     * 
     * @return
     */
    public RangeSelectListener getRangeSelectListener() {
        return rangeSelectListener;
    }

    private EventClickListener eventClickListener;

    /**
     * Get the listener that listens to the user clicking on the events
     */
    public EventClickListener getEventClickListener() {
        return eventClickListener;
    }

    /**
     * Set the listener that listens to the user clicking on the events
     * 
     * @param listener
     *            The listener to use
     */
    public void setListener(EventClickListener listener) {
        eventClickListener = listener;
    }

    private EventMovedListener eventMovedListener;

    /**
     * Get the listener that listens to when event is dragged to a new location
     * 
     * @return
     */
    public EventMovedListener getEventMovedListener() {
        return eventMovedListener;
    }

    /**
     * Set the listener that listens to when event is dragged to a new location
     * 
     * @param eventMovedListener
     *            The listener to use
     */
    public void setListener(EventMovedListener eventMovedListener) {
        this.eventMovedListener = eventMovedListener;
    }

    private ScrollListener scrollListener;

    /**
     * Get the listener that listens to when the calendar widget is scrolled
     * 
     * @return
     */
    public ScrollListener getScrollListener() {
        return scrollListener;
    }

    /**
     * Set the listener that listens to when the calendar widget is scrolled
     * 
     * @param scrollListener
     *            The listener to use
     */
    public void setListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    private EventResizeListener eventResizeListener;

    /**
     * Get the listener that listens to when an events time limits are being
     * adjusted
     * 
     * @return
     */
    public EventResizeListener getEventResizeListener() {
        return eventResizeListener;
    }

    /**
     * Set the listener that listens to when an events time limits are being
     * adjusted
     * 
     * @param eventResizeListener
     *            The listener to use
     */
    public void setListener(EventResizeListener eventResizeListener) {
        this.eventResizeListener = eventResizeListener;
    }

    private MouseEventListener mouseEventListener;
    private boolean forwardNavigationEnabled = true;
    private boolean backwardNavigationEnabled = true;
    private boolean eventCaptionAsHtml = false;

    /**
     * Get the listener that listen to mouse events
     * 
     * @return
     */
    public MouseEventListener getMouseEventListener() {
        return mouseEventListener;
    }

    /**
     * Set the listener that listen to mouse events
     * 
     * @param mouseEventListener
     *            The listener to use
     */
    public void setListener(MouseEventListener mouseEventListener) {
        this.mouseEventListener = mouseEventListener;
    }

    /**
     * Is selecting a range allowed?
     */
    public boolean isRangeSelectAllowed() {
        return rangeSelectAllowed;
    }

    /**
     * Set selecting a range allowed
     * 
     * @param rangeSelectAllowed
     *            Should selecting a range be allowed
     */
    public void setRangeSelectAllowed(boolean rangeSelectAllowed) {
        this.rangeSelectAllowed = rangeSelectAllowed;
    }

    /**
     * Is moving a range allowed
     * 
     * @return
     */
    public boolean isRangeMoveAllowed() {
        return rangeMoveAllowed;
    }

    /**
     * Is moving a range allowed
     * 
     * @param rangeMoveAllowed
     *            Is it allowed
     */
    public void setRangeMoveAllowed(boolean rangeMoveAllowed) {
        this.rangeMoveAllowed = rangeMoveAllowed;
    }

    /**
     * Is resizing an event allowed
     */
    public boolean isEventResizeAllowed() {
        return eventResizeAllowed;
    }

    /**
     * Is resizing an event allowed
     * 
     * @param eventResizeAllowed
     *            True if allowed false if not
     */
    public void setEventResizeAllowed(boolean eventResizeAllowed) {
        this.eventResizeAllowed = eventResizeAllowed;
    }

    /**
     * Is moving an event allowed
     */
    public boolean isEventMoveAllowed() {
        return eventMoveAllowed;
    }

    /**
     * Is moving an event allowed
     * 
     * @param eventMoveAllowed
     *            True if moving is allowed, false if not
     */
    public void setEventMoveAllowed(boolean eventMoveAllowed) {
        this.eventMoveAllowed = eventMoveAllowed;
    }

    public boolean isBackwardNavigationEnabled() {
        return backwardNavigationEnabled;
    }

    public void setBackwardNavigationEnabled(boolean enabled) {
        backwardNavigationEnabled = enabled;
    }

    public boolean isForwardNavigationEnabled() {
        return forwardNavigationEnabled;
    }

    public void setForwardNavigationEnabled(boolean enabled) {
        forwardNavigationEnabled = enabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.client.ui.dd.VHasDropHandler#getDropHandler()
     */
    @Override
    public CalendarDropHandler getDropHandler() {
        return dropHandler;
    }

    /**
     * Set the drop handler
     * 
     * @param dropHandler
     *            The drophandler to use
     */
    public void setDropHandler(CalendarDropHandler dropHandler) {
        this.dropHandler = dropHandler;
    }

    /**
     * Sets whether the event captions are rendered as HTML.
     * <p>
     * If set to true, the captions are rendered in the browser as HTML and the
     * developer is responsible for ensuring no harmful HTML is used. If set to
     * false, the caption is rendered in the browser as plain text.
     * <p>
     * The default is false, i.e. to render that caption as plain text.
     * 
     * @param captionAsHtml
     *            true if the captions are rendered as HTML, false if rendered
     *            as plain text
     */
    public void setEventCaptionAsHtml(boolean eventCaptionAsHtml) {
        this.eventCaptionAsHtml = eventCaptionAsHtml;
    }

    /**
     * Checks whether event captions are rendered as HTML
     * <p>
     * The default is false, i.e. to render that caption as plain text.
     * 
     * @return true if the captions are rendered as HTML, false if rendered as
     *         plain text
     */
    public boolean isEventCaptionAsHtml() {
        return eventCaptionAsHtml;
    }
}
