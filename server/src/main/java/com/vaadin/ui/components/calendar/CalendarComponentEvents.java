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
package com.vaadin.ui.components.calendar;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.EventListener;

import com.vaadin.shared.ui.calendar.CalendarEventId;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.util.ReflectTools;

/**
 * Interface for all Vaadin Calendar events.
 * 
 * @since 7.1.0
 * @author Vaadin Ltd.
 */
public interface CalendarComponentEvents extends Serializable {

    /**
     * Notifier interface for notifying listener of calendar events
     */
    public interface CalendarEventNotifier extends Serializable {
        /**
         * Get the assigned event handler for the given eventId.
         * 
         * @param eventId
         * @return the assigned eventHandler, or null if no handler is assigned
         */
        public EventListener getHandler(String eventId);
    }

    /**
     * Notifier interface for event drag & drops.
     */
    public interface EventMoveNotifier extends CalendarEventNotifier {

        /**
         * Set the EventMoveHandler.
         * 
         * @param listener
         *            EventMoveHandler to be added
         */
        public void setHandler(EventMoveHandler listener);

    }

    /**
     * MoveEvent is sent when existing event is dragged to a new position.
     */
    @SuppressWarnings("serial")
    public class MoveEvent extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.EVENTMOVE;

        /** Index for the moved Schedule.Event. */
        private CalendarEvent calendarEvent;

        /** New starting date for the moved Calendar.Event. */
        private Date newStart;

        /**
         * MoveEvent needs the target event and new start date.
         * 
         * @param source
         *            Calendar component.
         * @param calendarEvent
         *            Target event.
         * @param newStart
         *            Target event's new start date.
         */
        public MoveEvent(Calendar source, CalendarEvent calendarEvent,
                Date newStart) {
            super(source);

            this.calendarEvent = calendarEvent;
            this.newStart = newStart;
        }

        /**
         * Get target event.
         * 
         * @return Target event.
         */
        public CalendarEvent getCalendarEvent() {
            return calendarEvent;
        }

        /**
         * Get new start date.
         * 
         * @return New start date.
         */
        public Date getNewStart() {
            return newStart;
        }
    }

    /**
     * Handler interface for when events are being dragged on the calendar
     * 
     */
    public interface EventMoveHandler extends EventListener, Serializable {

        /** Trigger method for the MoveEvent. */
        public static final Method eventMoveMethod = ReflectTools.findMethod(
                EventMoveHandler.class, "eventMove", MoveEvent.class);

        /**
         * This method will be called when event has been moved to a new
         * position.
         * 
         * @param event
         *            MoveEvent containing specific information of the new
         *            position and target event.
         */
        public void eventMove(MoveEvent event);
    }

    /**
     * Handler interface for day or time cell drag-marking with mouse.
     */
    public interface RangeSelectNotifier extends Serializable,
            CalendarEventNotifier {

        /**
         * Set the RangeSelectHandler that listens for drag-marking.
         * 
         * @param listener
         *            RangeSelectHandler to be added.
         */
        public void setHandler(RangeSelectHandler listener);
    }

    /**
     * RangeSelectEvent is sent when day or time cells are drag-marked with
     * mouse.
     */
    @SuppressWarnings("serial")
    public class RangeSelectEvent extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.RANGESELECT;

        /** Calendar event's start date. */
        private Date start;

        /** Calendar event's end date. */
        private Date end;

        /**
         * Defines the event's view mode.
         */
        private boolean monthlyMode;

        /**
         * RangeSelectEvent needs a start and end date.
         * 
         * @param source
         *            Calendar component.
         * @param start
         *            Start date.
         * @param end
         *            End date.
         * @param monthlyMode
         *            Calendar view mode.
         */
        public RangeSelectEvent(Calendar source, Date start, Date end,
                boolean monthlyMode) {
            super(source);
            this.start = start;
            this.end = end;
            this.monthlyMode = monthlyMode;
        }

        /**
         * Get start date.
         * 
         * @return Start date.
         */
        public Date getStart() {
            return start;
        }

        /**
         * Get end date.
         * 
         * @return End date.
         */
        public Date getEnd() {
            return end;
        }

        /**
         * Gets the event's view mode. Calendar can be be either in monthly or
         * weekly mode, depending on the active date range.
         * 
         * @deprecated User {@link Calendar#isMonthlyMode()} instead
         * 
         * @return Returns true when monthly view is active.
         */
        @Deprecated
        public boolean isMonthlyMode() {
            return monthlyMode;
        }
    }

    /** RangeSelectHandler handles RangeSelectEvent. */
    public interface RangeSelectHandler extends EventListener, Serializable {

        /** Trigger method for the RangeSelectEvent. */
        public static final Method rangeSelectMethod = ReflectTools
                .findMethod(RangeSelectHandler.class, "rangeSelect",
                        RangeSelectEvent.class);

        /**
         * This method will be called when day or time cells are drag-marked
         * with mouse.
         * 
         * @param event
         *            RangeSelectEvent that contains range start and end date.
         */
        public void rangeSelect(RangeSelectEvent event);
    }

    /** Notifier interface for navigation listening. */
    public interface NavigationNotifier extends Serializable {
        /**
         * Add a forward navigation listener.
         * 
         * @param handler
         *            ForwardHandler to be added.
         */
        public void setHandler(ForwardHandler handler);

        /**
         * Add a backward navigation listener.
         * 
         * @param handler
         *            BackwardHandler to be added.
         */
        public void setHandler(BackwardHandler handler);

        /**
         * Add a date click listener.
         * 
         * @param handler
         *            DateClickHandler to be added.
         */
        public void setHandler(DateClickHandler handler);

        /**
         * Add a event click listener.
         * 
         * @param handler
         *            EventClickHandler to be added.
         */
        public void setHandler(EventClickHandler handler);

        /**
         * Add a week click listener.
         * 
         * @param handler
         *            WeekClickHandler to be added.
         */
        public void setHandler(WeekClickHandler handler);
    }

    /**
     * ForwardEvent is sent when forward navigation button is clicked.
     */
    @SuppressWarnings("serial")
    public class ForwardEvent extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.FORWARD;

        /**
         * ForwardEvent needs only the source component.
         * 
         * @param source
         *            Calendar component.
         */
        public ForwardEvent(Calendar source) {
            super(source);
        }
    }

    /** ForwardHandler handles ForwardEvent. */
    public interface ForwardHandler extends EventListener, Serializable {

        /** Trigger method for the ForwardEvent. */
        public static final Method forwardMethod = ReflectTools.findMethod(
                ForwardHandler.class, "forward", ForwardEvent.class);

        /**
         * This method will be called when date range is moved forward.
         * 
         * @param event
         *            ForwardEvent
         */
        public void forward(ForwardEvent event);
    }

    /**
     * BackwardEvent is sent when backward navigation button is clicked.
     */
    @SuppressWarnings("serial")
    public class BackwardEvent extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.BACKWARD;

        /**
         * BackwardEvent needs only the source source component.
         * 
         * @param source
         *            Calendar component.
         */
        public BackwardEvent(Calendar source) {
            super(source);
        }
    }

    /** BackwardHandler handles BackwardEvent. */
    public interface BackwardHandler extends EventListener, Serializable {

        /** Trigger method for the BackwardEvent. */
        public static final Method backwardMethod = ReflectTools.findMethod(
                BackwardHandler.class, "backward", BackwardEvent.class);

        /**
         * This method will be called when date range is moved backwards.
         * 
         * @param event
         *            BackwardEvent
         */
        public void backward(BackwardEvent event);
    }

    /**
     * DateClickEvent is sent when a date is clicked.
     */
    @SuppressWarnings("serial")
    public class DateClickEvent extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.DATECLICK;

        /** Date that was clicked. */
        private Date date;

        /** DateClickEvent needs the target date that was clicked. */
        public DateClickEvent(Calendar source, Date date) {
            super(source);
            this.date = date;
        }

        /**
         * Get clicked date.
         * 
         * @return Clicked date.
         */
        public Date getDate() {
            return date;
        }
    }

    /** DateClickHandler handles DateClickEvent. */
    public interface DateClickHandler extends EventListener, Serializable {

        /** Trigger method for the DateClickEvent. */
        public static final Method dateClickMethod = ReflectTools.findMethod(
                DateClickHandler.class, "dateClick", DateClickEvent.class);

        /**
         * This method will be called when a date is clicked.
         * 
         * @param event
         *            DateClickEvent containing the target date.
         */
        public void dateClick(DateClickEvent event);
    }

    /**
     * EventClick is sent when an event is clicked.
     */
    @SuppressWarnings("serial")
    public class EventClick extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.EVENTCLICK;

        /** Clicked source event. */
        private CalendarEvent calendarEvent;

        /** Target source event is needed for the EventClick. */
        public EventClick(Calendar source, CalendarEvent calendarEvent) {
            super(source);
            this.calendarEvent = calendarEvent;
        }

        /**
         * Get the clicked event.
         * 
         * @return Clicked event.
         */
        public CalendarEvent getCalendarEvent() {
            return calendarEvent;
        }
    }

    /** EventClickHandler handles EventClick. */
    public interface EventClickHandler extends EventListener, Serializable {

        /** Trigger method for the EventClick. */
        public static final Method eventClickMethod = ReflectTools.findMethod(
                EventClickHandler.class, "eventClick", EventClick.class);

        /**
         * This method will be called when an event is clicked.
         * 
         * @param event
         *            EventClick containing the target event.
         */
        public void eventClick(EventClick event);
    }

    /**
     * WeekClick is sent when week is clicked.
     */
    @SuppressWarnings("serial")
    public class WeekClick extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.WEEKCLICK;

        /** Target week. */
        private int week;

        /** Target year. */
        private int year;

        /**
         * WeekClick needs a target year and week.
         * 
         * @param source
         *            Target source.
         * @param week
         *            Target week.
         * @param year
         *            Target year.
         */
        public WeekClick(Calendar source, int week, int year) {
            super(source);
            this.week = week;
            this.year = year;
        }

        /**
         * Get week as a integer. See {@link java.util.Calendar} for the allowed
         * values.
         * 
         * @return Week as a integer.
         */
        public int getWeek() {
            return week;
        }

        /**
         * Get year as a integer. See {@link java.util.Calendar} for the allowed
         * values.
         * 
         * @return Year as a integer
         */
        public int getYear() {
            return year;
        }
    }

    /** WeekClickHandler handles WeekClicks. */
    public interface WeekClickHandler extends EventListener, Serializable {

        /** Trigger method for the WeekClick. */
        public static final Method weekClickMethod = ReflectTools.findMethod(
                WeekClickHandler.class, "weekClick", WeekClick.class);

        /**
         * This method will be called when a week is clicked.
         * 
         * @param event
         *            WeekClick containing the target week and year.
         */
        public void weekClick(WeekClick event);
    }

    /**
     * EventResize is sent when an event is resized
     */
    @SuppressWarnings("serial")
    public class EventResize extends CalendarComponentEvent {

        public static final String EVENT_ID = CalendarEventId.EVENTRESIZE;

        private CalendarEvent calendarEvent;

        private Date startTime;

        private Date endTime;

        public EventResize(Calendar source, CalendarEvent calendarEvent,
                Date startTime, Date endTime) {
            super(source);
            this.calendarEvent = calendarEvent;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        /**
         * Get target event.
         * 
         * @return Target event.
         */
        public CalendarEvent getCalendarEvent() {
            return calendarEvent;
        }

        /**
         * @deprecated Use {@link #getNewStart()} instead
         * 
         * @return the new start time
         */
        @Deprecated
        public Date getNewStartTime() {
            return startTime;
        }

        /**
         * Returns the updated start date/time of the event
         * 
         * @return The new date for the event
         */
        public Date getNewStart() {
            return startTime;
        }

        /**
         * @deprecated Use {@link #getNewEnd()} instead
         * 
         * @return the new end time
         */
        @Deprecated
        public Date getNewEndTime() {
            return endTime;
        }

        /**
         * Returns the updates end date/time of the event
         * 
         * @return The new date for the event
         */
        public Date getNewEnd() {
            return endTime;
        }
    }

    /**
     * Notifier interface for event resizing.
     */
    public interface EventResizeNotifier extends Serializable {

        /**
         * Set a EventResizeHandler.
         * 
         * @param handler
         *            EventResizeHandler to be set
         */
        public void setHandler(EventResizeHandler handler);
    }

    /**
     * Handler for EventResize event.
     */
    public interface EventResizeHandler extends EventListener, Serializable {

        /** Trigger method for the EventResize. */
        public static final Method eventResizeMethod = ReflectTools.findMethod(
                EventResizeHandler.class, "eventResize", EventResize.class);

        void eventResize(EventResize event);
    }

}
