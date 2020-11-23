/*
 * Copyright 2000-2020 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.client.ui.calendar.schedule;

/**
 * Utility class used to represent a day when updating views. Only used
 * internally.
 *
 * @since 7.1
 * @author Vaadin Ltd.
 */
public class CalendarDay {
    private String date;
    private String localizedDateFormat;
    private int dayOfWeek;
    private int week;
    private int yearOfWeek;

    public CalendarDay(String date, String localizedDateFormat, int dayOfWeek,
            int week, int yearOfWeek) {
        super();
        this.date = date;
        this.localizedDateFormat = localizedDateFormat;
        this.dayOfWeek = dayOfWeek;
        this.week = week;
        this.yearOfWeek = yearOfWeek;
    }

    public String getDate() {
        return date;
    }

    public String getLocalizedDateFormat() {
        return localizedDateFormat;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public int getWeek() {
        return week;
    }

    public int getYearOfWeek() {
        return yearOfWeek;
    }
}
