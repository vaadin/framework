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
package com.vaadin.client.ui.calendar.schedule;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.vaadin.client.Util;
import com.vaadin.shared.ui.calendar.DateConstants;

/**
 * Internally used by the calendar
 * 
 * @since 7.1
 */
public class DateCellDayEvent extends FocusableHTML implements
        MouseDownHandler, MouseUpHandler, MouseMoveHandler, KeyDownHandler,
        ContextMenuHandler, HasTooltipKey {

    private final DateCell dateCell;
    private Element caption = null;
    private final Element eventContent;
    private CalendarEvent calendarEvent = null;
    private HandlerRegistration moveRegistration;
    private int startY = -1;
    private int startX = -1;
    private String moveWidth;
    public static final int halfHourInMilliSeconds = 1800 * 1000;
    private Date startDatetimeFrom;
    private Date startDatetimeTo;
    private boolean mouseMoveStarted;
    private int top;
    private int startYrelative;
    private int startXrelative;
    private boolean disabled;
    private final WeekGrid weekGrid;
    private Element topResizeBar;
    private Element bottomResizeBar;
    private Element clickTarget;
    private final Integer eventIndex;
    private int slotHeight;
    private final List<HandlerRegistration> handlers;
    private boolean mouseMoveCanceled;

    public DateCellDayEvent(DateCell dateCell, WeekGrid parent,
            CalendarEvent event) {
        super();
        this.dateCell = dateCell;

        handlers = new LinkedList<HandlerRegistration>();

        setStylePrimaryName("v-calendar-event");
        setCalendarEvent(event);

        weekGrid = parent;

        Style s = getElement().getStyle();
        if (event.getStyleName().length() > 0) {
            addStyleDependentName(event.getStyleName());
        }
        s.setPosition(Position.ABSOLUTE);

        caption = DOM.createDiv();
        caption.addClassName("v-calendar-event-caption");
        getElement().appendChild(caption);

        eventContent = DOM.createDiv();
        eventContent.addClassName("v-calendar-event-content");
        getElement().appendChild(eventContent);

        if (weekGrid.getCalendar().isEventResizeAllowed()) {
            topResizeBar = DOM.createDiv();
            bottomResizeBar = DOM.createDiv();

            topResizeBar.addClassName("v-calendar-event-resizetop");
            bottomResizeBar.addClassName("v-calendar-event-resizebottom");

            getElement().appendChild(topResizeBar);
            getElement().appendChild(bottomResizeBar);
        }

        eventIndex = event.getIndex();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        handlers.add(addMouseDownHandler(this));
        handlers.add(addMouseUpHandler(this));
        handlers.add(addKeyDownHandler(this));
        handlers.add(addDomHandler(this, ContextMenuEvent.getType()));
    }

    @Override
    protected void onDetach() {
        for (HandlerRegistration handler : handlers) {
            handler.removeHandler();
        }
        handlers.clear();
        super.onDetach();
    }

    public void setSlotHeightInPX(int slotHeight) {
        this.slotHeight = slotHeight;
    }

    public void updatePosition(long startFromMinutes, long durationInMinutes) {
        if (startFromMinutes < 0) {
            startFromMinutes = 0;
        }
        top = weekGrid.getPixelTopFor((int) startFromMinutes);

        getElement().getStyle().setTop(top, Unit.PX);
        if (durationInMinutes > 0) {
            int heightMinutes = weekGrid.getPixelLengthFor(
                    (int) startFromMinutes, (int) durationInMinutes);
            setHeight(heightMinutes);
        } else {
            setHeight(-1);
        }

        boolean multiRowCaption = (durationInMinutes > 30);
        updateCaptions(multiRowCaption);
    }

    public int getTop() {
        return top;
    }

    public void setMoveWidth(int width) {
        moveWidth = width + "px";
    }

    public void setHeight(int h) {
        if (h == -1) {
            getElement().getStyle().setProperty("height", "");
            eventContent.getStyle().setProperty("height", "");
        } else {
            getElement().getStyle().setHeight(h, Unit.PX);
            // FIXME measure the border height (2px) from the DOM
            eventContent.getStyle().setHeight(h - 2, Unit.PX);
        }
    }

    /**
     * @param bigMode
     *            If false, event is so small that caption must be in time-row
     */
    private void updateCaptions(boolean bigMode) {
        String innerHtml;
        String escapedCaption = Util.escapeHTML(calendarEvent.getCaption());
        String timeAsText = calendarEvent.getTimeAsText();
        if (bigMode) {
            innerHtml = "<span>" + timeAsText + "</span><br />"
                    + escapedCaption;
        } else {
            innerHtml = "<span>" + timeAsText + "<span>:</span></span> "
                    + escapedCaption;
        }
        caption.setInnerHTML(innerHtml);
        eventContent.setInnerHTML("");
    }

    @Override
    public void onKeyDown(KeyDownEvent event) {
        int keycode = event.getNativeEvent().getKeyCode();
        if (keycode == KeyCodes.KEY_ESCAPE && mouseMoveStarted) {
            cancelMouseMove();
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        startX = event.getClientX();
        startY = event.getClientY();
        if (isDisabled() || event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
            return;
        }

        clickTarget = Element.as(event.getNativeEvent().getEventTarget());
        mouseMoveCanceled = false;

        if (weekGrid.getCalendar().isEventMoveAllowed() || clickTargetsResize()) {
            moveRegistration = addMouseMoveHandler(this);
            setFocus(true);
            try {
                startYrelative = (int) ((double) event.getRelativeY(caption) % slotHeight);
                startXrelative = (event.getRelativeX(weekGrid.getElement()) - weekGrid.timebar
                        .getOffsetWidth()) % getDateCellWidth();
            } catch (Exception e) {
                GWT.log("Exception calculating relative start position", e);
            }
            mouseMoveStarted = false;
            Style s = getElement().getStyle();
            s.setZIndex(1000);
            startDatetimeFrom = (Date) calendarEvent.getStartTime().clone();
            startDatetimeTo = (Date) calendarEvent.getEndTime().clone();
            Event.setCapture(getElement());
        }

        // make sure the right cursor is always displayed
        if (clickTargetsResize()) {
            addGlobalResizeStyle();
        }

        /*
         * We need to stop the event propagation or else the WeekGrid range
         * select will kick in
         */
        event.stopPropagation();
        event.preventDefault();
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        if (mouseMoveCanceled
                || event.getNativeButton() != NativeEvent.BUTTON_LEFT) {
            return;
        }

        Event.releaseCapture(getElement());
        setFocus(false);
        if (moveRegistration != null) {
            moveRegistration.removeHandler();
            moveRegistration = null;
        }
        int endX = event.getClientX();
        int endY = event.getClientY();
        int xDiff = startX - endX;
        int yDiff = startY - endY;
        startX = -1;
        startY = -1;
        mouseMoveStarted = false;
        Style s = getElement().getStyle();
        s.setZIndex(1);
        if (!clickTargetsResize()) {
            // check if mouse has moved over threshold of 3 pixels
            boolean mouseMoved = (xDiff < -3 || xDiff > 3 || yDiff < -3 || yDiff > 3);

            if (!weekGrid.getCalendar().isDisabledOrReadOnly() && mouseMoved) {
                // Event Move:
                // - calendar must be enabled
                // - calendar must not be in read-only mode
                weekGrid.eventMoved(this);
            } else if (!weekGrid.getCalendar().isDisabled()) {
                // Event Click:
                // - calendar must be enabled (read-only is allowed)
                EventTarget et = event.getNativeEvent().getEventTarget();
                Element e = Element.as(et);
                if (e == caption || e == eventContent
                        || e.getParentElement() == caption) {
                    if (weekGrid.getCalendar().getEventClickListener() != null) {
                        weekGrid.getCalendar().getEventClickListener()
                                .eventClick(calendarEvent);
                    }
                }
            }

        } else { // click targeted resize bar
            removeGlobalResizeStyle();
            if (weekGrid.getCalendar().getEventResizeListener() != null) {
                weekGrid.getCalendar().getEventResizeListener()
                        .eventResized(calendarEvent);
            }
            dateCell.recalculateEventWidths();
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onMouseMove(MouseMoveEvent event) {
        if (startY < 0 && startX < 0) {
            return;
        }
        if (isDisabled()) {
            Event.releaseCapture(getElement());
            mouseMoveStarted = false;
            startY = -1;
            startX = -1;
            removeGlobalResizeStyle();
            return;
        }
        int currentY = event.getClientY();
        int currentX = event.getClientX();
        int moveY = (currentY - startY);
        int moveX = (currentX - startX);
        if ((moveY < 5 && moveY > -6) && (moveX < 5 && moveX > -6)) {
            return;
        }
        if (!mouseMoveStarted) {
            setWidth(moveWidth);
            getElement().getStyle().setMarginLeft(0, Unit.PX);
            mouseMoveStarted = true;
        }

        HorizontalPanel parent = (HorizontalPanel) getParent().getParent();
        int relativeX = event.getRelativeX(parent.getElement())
                - weekGrid.timebar.getOffsetWidth();
        int halfHourDiff = 0;
        if (moveY > 0) {
            halfHourDiff = (startYrelative + moveY) / slotHeight;
        } else {
            halfHourDiff = (moveY - startYrelative) / slotHeight;
        }

        int dateCellWidth = getDateCellWidth();
        long dayDiff = 0;
        if (moveX >= 0) {
            dayDiff = (startXrelative + moveX) / dateCellWidth;
        } else {
            dayDiff = (moveX - (dateCellWidth - startXrelative))
                    / dateCellWidth;
        }

        int dayOffset = relativeX / dateCellWidth;

        // sanity check for right side overflow
        int dateCellCount = weekGrid.getDateCellCount();
        if (dayOffset >= dateCellCount) {
            dayOffset--;
            dayDiff--;
        }

        int dayOffsetPx = calculateDateCellOffsetPx(dayOffset)
                + weekGrid.timebar.getOffsetWidth();

        GWT.log("DateCellWidth: " + dateCellWidth + " dayDiff: " + dayDiff
                + " dayOffset: " + dayOffset + " dayOffsetPx: " + dayOffsetPx
                + " startXrelative: " + startXrelative + " moveX: " + moveX);

        if (relativeX < 0 || relativeX >= getDatesWidth()) {
            return;
        }

        Style s = getElement().getStyle();

        Date from = calendarEvent.getStartTime();
        Date to = calendarEvent.getEndTime();
        long duration = to.getTime() - from.getTime();

        if (!clickTargetsResize()
                && weekGrid.getCalendar().isEventMoveAllowed()) {
            long daysMs = dayDiff * DateConstants.DAYINMILLIS;
            from.setTime(startDatetimeFrom.getTime() + daysMs);
            from.setTime(from.getTime()
                    + ((long) halfHourInMilliSeconds * halfHourDiff));
            to.setTime((from.getTime() + duration));

            calendarEvent.setStartTime(from);
            calendarEvent.setEndTime(to);
            calendarEvent.setStart(new Date(from.getTime()));
            calendarEvent.setEnd(new Date(to.getTime()));

            // Set new position for the event
            long startFromMinutes = (from.getHours() * 60) + from.getMinutes();
            long range = calendarEvent.getRangeInMinutes();
            startFromMinutes = calculateStartFromMinute(startFromMinutes, from,
                    to, dayOffsetPx);
            if (startFromMinutes < 0) {
                range += startFromMinutes;
            }
            updatePosition(startFromMinutes, range);

            s.setLeft(dayOffsetPx, Unit.PX);

            if (weekGrid.getDateCellWidths() != null) {
                s.setWidth(weekGrid.getDateCellWidths()[dayOffset], Unit.PX);
            } else {
                setWidth(moveWidth);
            }

        } else if (clickTarget == topResizeBar) {
            long oldStartTime = startDatetimeFrom.getTime();
            long newStartTime = oldStartTime
                    + ((long) halfHourInMilliSeconds * halfHourDiff);

            if (!isTimeRangeTooSmall(newStartTime, startDatetimeTo.getTime())) {
                newStartTime = startDatetimeTo.getTime() - getMinTimeRange();
            }

            from.setTime(newStartTime);

            calendarEvent.setStartTime(from);
            calendarEvent.setStart(new Date(from.getTime()));

            // Set new position for the event
            long startFromMinutes = (from.getHours() * 60) + from.getMinutes();
            long range = calendarEvent.getRangeInMinutes();

            updatePosition(startFromMinutes, range);

        } else if (clickTarget == bottomResizeBar) {
            long oldEndTime = startDatetimeTo.getTime();
            long newEndTime = oldEndTime
                    + ((long) halfHourInMilliSeconds * halfHourDiff);

            if (!isTimeRangeTooSmall(startDatetimeFrom.getTime(), newEndTime)) {
                newEndTime = startDatetimeFrom.getTime() + getMinTimeRange();
            }

            to.setTime(newEndTime);

            calendarEvent.setEndTime(to);
            calendarEvent.setEnd(new Date(to.getTime()));

            // Set new position for the event
            long startFromMinutes = (startDatetimeFrom.getHours() * 60)
                    + startDatetimeFrom.getMinutes();
            long range = calendarEvent.getRangeInMinutes();
            startFromMinutes = calculateStartFromMinute(startFromMinutes, from,
                    to, dayOffsetPx);
            if (startFromMinutes < 0) {
                range += startFromMinutes;
            }
            updatePosition(startFromMinutes, range);
        }
    }

    private void cancelMouseMove() {
        mouseMoveCanceled = true;

        // reset and remove everything related to the event handling
        Event.releaseCapture(getElement());
        setFocus(false);

        if (moveRegistration != null) {
            moveRegistration.removeHandler();
            moveRegistration = null;
        }

        mouseMoveStarted = false;
        removeGlobalResizeStyle();

        Style s = getElement().getStyle();
        s.setZIndex(1);

        // reset the position of the event
        int dateCellWidth = getDateCellWidth();
        int dayOffset = startXrelative / dateCellWidth;
        s.clearLeft();

        calendarEvent.setStartTime(startDatetimeFrom);
        calendarEvent.setEndTime(startDatetimeTo);

        long startFromMinutes = (startDatetimeFrom.getHours() * 60)
                + startDatetimeFrom.getMinutes();
        long range = calendarEvent.getRangeInMinutes();

        startFromMinutes = calculateStartFromMinute(startFromMinutes,
                startDatetimeFrom, startDatetimeTo, dayOffset);
        if (startFromMinutes < 0) {
            range += startFromMinutes;
        }

        updatePosition(startFromMinutes, range);

        startY = -1;
        startX = -1;

        // to reset the event width
        ((DateCell) getParent()).recalculateEventWidths();
    }

    // date methods are not deprecated in GWT
    @SuppressWarnings("deprecation")
    private long calculateStartFromMinute(long startFromMinutes, Date from,
            Date to, int dayOffset) {
        boolean eventStartAtDifferentDay = from.getDate() != to.getDate();
        if (eventStartAtDifferentDay) {
            long minutesOnPrevDay = (getTargetDateByCurrentPosition(dayOffset)
                    .getTime() - from.getTime()) / DateConstants.MINUTEINMILLIS;
            startFromMinutes = -1 * minutesOnPrevDay;
        }

        return startFromMinutes;
    }

    /**
     * @param dateOffset
     * @return the amount of pixels the given date is from the left side
     */
    private int calculateDateCellOffsetPx(int dateOffset) {
        int dateCellOffset = 0;
        int[] dateWidths = weekGrid.getDateCellWidths();

        if (dateWidths != null) {
            for (int i = 0; i < dateOffset; i++) {
                dateCellOffset += dateWidths[i] + 1;
            }
        } else {
            dateCellOffset = dateOffset * weekGrid.getDateCellWidth();
        }

        return dateCellOffset;
    }

    /**
     * Check if the given time range is too small for events
     * 
     * @param start
     * @param end
     * @return
     */
    private boolean isTimeRangeTooSmall(long start, long end) {
        return (end - start) >= getMinTimeRange();
    }

    /**
     * @return the minimum amount of ms that an event must last when resized
     */
    private long getMinTimeRange() {
        return DateConstants.MINUTEINMILLIS * 30;
    }

    /**
     * Build the string for sending resize events to server
     * 
     * @param event
     * @return
     */
    private String buildResizeString(CalendarEvent event) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(event.getIndex());
        buffer.append(",");
        buffer.append(DateUtil.formatClientSideDate(event.getStart()));
        buffer.append("-");
        buffer.append(DateUtil.formatClientSideTime(event.getStartTime()));
        buffer.append(",");
        buffer.append(DateUtil.formatClientSideDate(event.getEnd()));
        buffer.append("-");
        buffer.append(DateUtil.formatClientSideTime(event.getEndTime()));

        return buffer.toString();
    }

    private Date getTargetDateByCurrentPosition(int left) {
        DateCell newParent = (DateCell) weekGrid.content
                .getWidget((left / getDateCellWidth()) + 1);
        Date targetDate = newParent.getDate();
        return targetDate;
    }

    private int getDateCellWidth() {
        return weekGrid.getDateCellWidth();
    }

    /* Returns total width of all date cells. */
    private int getDatesWidth() {
        if (weekGrid.width == -1) {
            // Undefined width. Needs to be calculated by the known cell
            // widths.
            int count = weekGrid.content.getWidgetCount() - 1;
            return count * getDateCellWidth();
        }

        return weekGrid.getInternalWidth();
    }

    /**
     * @return true if the current mouse movement is resizing
     */
    private boolean clickTargetsResize() {
        return weekGrid.getCalendar().isEventResizeAllowed()
                && (clickTarget == topResizeBar || clickTarget == bottomResizeBar);
    }

    private void addGlobalResizeStyle() {
        if (clickTarget == topResizeBar) {
            weekGrid.getCalendar().addStyleDependentName("nresize");
        } else if (clickTarget == bottomResizeBar) {
            weekGrid.getCalendar().addStyleDependentName("sresize");
        }
    }

    private void removeGlobalResizeStyle() {
        weekGrid.getCalendar().removeStyleDependentName("nresize");
        weekGrid.getCalendar().removeStyleDependentName("sresize");
    }

    public void setCalendarEvent(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    public CalendarEvent getCalendarEvent() {
        return calendarEvent;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void onContextMenu(ContextMenuEvent event) {
        if (dateCell.weekgrid.getCalendar().getMouseEventListener() != null) {
            event.preventDefault();
            event.stopPropagation();
            dateCell.weekgrid.getCalendar().getMouseEventListener()
                    .contextMenu(event, this);
        }
    }

    @Override
    public Object getTooltipKey() {
        return eventIndex;
    }
}
