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

import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.ui.VCalendar;

/**
 * Represents a cell used in {@link WeeklyLongEvents}
 * 
 * @since 7.1
 */
public class WeeklyLongEventsDateCell extends HTML implements HasTooltipKey {
    private Date date;
    private CalendarEvent calendarEvent;
    private VCalendar calendar;

    public WeeklyLongEventsDateCell() {
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setEvent(CalendarEvent event) {
        calendarEvent = event;
    }

    public CalendarEvent getEvent() {
        return calendarEvent;
    }

    public void setCalendar(VCalendar calendar) {
        this.calendar = calendar;
    }

    public VCalendar getCalendar() {
        return calendar;
    }

    @Override
    public Object getTooltipKey() {
        if (calendarEvent != null) {
            return calendarEvent.getIndex();
        }
        return null;
    }
}
