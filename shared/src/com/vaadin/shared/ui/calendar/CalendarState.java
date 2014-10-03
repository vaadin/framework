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
package com.vaadin.shared.ui.calendar;

import java.util.List;

import com.vaadin.shared.AbstractComponentState;

/**
 * @since 7.1.0
 * @author Vaadin Ltd.
 */
public class CalendarState extends AbstractComponentState {

    public boolean format24H;
    public String[] dayNames;
    public String[] monthNames;
    public int firstVisibleDayOfWeek = 1;
    public int lastVisibleDayOfWeek = 7;
    public int firstHourOfDay = 0;
    public int lastHourOfDay = 23;
    public int firstDayOfWeek;
    public int scroll;
    public String now;
    public List<CalendarState.Day> days;
    public List<CalendarState.Event> events;
    public List<CalendarState.Action> actions;

    public static class Day implements java.io.Serializable {
        public String date;
        public String localizedDateFormat;
        public int dayOfWeek;
        public int week;
        public int yearOfWeek;
    }

    public static class Action implements java.io.Serializable {

        public String caption;
        public String iconKey;
        public String actionKey;
        public String startDate;
        public String endDate;
    }

    public static class Event implements java.io.Serializable {
        public int index;
        public String caption;
        public String dateFrom;
        public String dateTo;
        public String timeFrom;
        public String timeTo;
        public String styleName;
        public String description;
        public boolean allDay;
    }
}
