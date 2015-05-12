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
package com.vaadin.tests.server.component.calendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.Calendar.TimeFormat;

public class CalendarDeclarativeTest extends DeclarativeTestBase<Calendar> {

    @Test
    public void testEmpty() {
        verifyDeclarativeDesign("<v-calendar></v-calendar>", new Calendar());
    }

    @Test
    public void testCalendarAllFeatures() throws ParseException {
        String design = "<v-calendar start-date='2014-11-17' end-date='2014-11-23' "
                + "first-visible-day-of-week=2 last-visible-day-of-week=5 "
                + "time-zone='EST' time-format='12h' first-visible-hour-of-day=8 "
                + "last-visible-hour-of-day=18 weekly-caption-format='mmm MM/dd' />";

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new Calendar();
        calendar.setStartDate(format.parse("2014-11-17"));
        calendar.setEndDate(format.parse("2014-11-23"));
        calendar.setFirstVisibleDayOfWeek(2);
        calendar.setLastVisibleDayOfWeek(5);
        calendar.setTimeZone(TimeZone.getTimeZone("EST"));
        calendar.setTimeFormat(TimeFormat.Format12H);
        calendar.setFirstVisibleHourOfDay(8);
        calendar.setLastVisibleHourOfDay(18);
        calendar.setWeeklyCaptionFormat("mmm MM/dd");
        verifyDeclarativeDesign(design, calendar);
    }

    protected void verifyDeclarativeDesign(String design, Calendar expected) {
        testRead(design, expected);
        testWrite(design, expected);
    }
}
