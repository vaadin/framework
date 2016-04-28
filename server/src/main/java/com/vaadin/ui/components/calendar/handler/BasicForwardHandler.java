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

import java.util.Calendar;
import java.util.Date;

import com.vaadin.shared.ui.calendar.DateConstants;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.ForwardHandler;

/**
 * Implements basic functionality needed to enable forward navigation.
 * 
 * @since 7.1
 * @author Vaadin Ltd.
 */
@SuppressWarnings("serial")
public class BasicForwardHandler implements ForwardHandler {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardHandler#forward
     * (com.vaadin.addon.calendar.ui.CalendarComponentEvents.ForwardEvent)
     */
    @Override
    public void forward(ForwardEvent event) {
        Date start = event.getComponent().getStartDate();
        Date end = event.getComponent().getEndDate();

        // calculate amount to move forward
        int durationInDays = (int) (((end.getTime()) - start.getTime()) / DateConstants.DAYINMILLIS);
        // for week view durationInDays = 7, for day view durationInDays = 1
        durationInDays++;

        // set new start and end times
        Calendar javaCalendar = Calendar.getInstance();
        javaCalendar.setTime(start);
        javaCalendar.add(java.util.Calendar.DATE, durationInDays);
        Date newStart = javaCalendar.getTime();

        javaCalendar.setTime(end);
        javaCalendar.add(java.util.Calendar.DATE, durationInDays);
        Date newEnd = javaCalendar.getTime();

        if (start.equals(end)) { // day view
            int firstDay = event.getComponent().getFirstVisibleDayOfWeek();
            int lastDay = event.getComponent().getLastVisibleDayOfWeek();
            int dayOfWeek = javaCalendar.get(Calendar.DAY_OF_WEEK);

            // we suppose that 7 >= lastDay >= firstDay >= 1
            while (!(firstDay <= dayOfWeek && dayOfWeek <= lastDay)) {
                javaCalendar.add(java.util.Calendar.DATE, 1);
                dayOfWeek = javaCalendar.get(Calendar.DAY_OF_WEEK);
            }

            newStart = javaCalendar.getTime();
            newEnd = javaCalendar.getTime();
        }

        setDates(event, newStart, newEnd);
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
    protected void setDates(ForwardEvent event, Date start, Date end) {
        event.getComponent().setStartDate(start);
        event.getComponent().setEndDate(end);
    }
}
