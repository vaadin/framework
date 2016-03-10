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
package com.vaadin.ui.components.calendar.handler;

import java.util.Date;

import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventMoveHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.MoveEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.EditableCalendarEvent;

/**
 * Implements basic functionality needed to enable moving events.
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class BasicEventMoveHandler implements EventMoveHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.EventMoveHandler
     * #eventMove
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.MoveEvent)
     */
    @Override
    public void eventMove(MoveEvent event) {
        CalendarEvent calendarEvent = event.getCalendarEvent();

        if (calendarEvent instanceof EditableCalendarEvent) {

            EditableCalendarEvent editableEvent = (EditableCalendarEvent) calendarEvent;

            Date newFromTime = event.getNewStart();

            // Update event dates
            long length = editableEvent.getEnd().getTime()
                    - editableEvent.getStart().getTime();
            setDates(editableEvent, newFromTime, new Date(newFromTime.getTime()
                    + length));
        }
    }

    /**
     * Set the start and end dates for the event
     * 
     * @param event
     *            The event that the start and end dates should be set
     * @param start
     *            The start date
     * @param end
     *            The end date
     */
    protected void setDates(EditableCalendarEvent event, Date start, Date end) {
        event.setStart(start);
        event.setEnd(end);
    }
}
