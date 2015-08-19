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
package com.vaadin.tests.server.component.datefield;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.DateField;

/**
 * Tests the declarative support for implementations of {@link DateField}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DateFieldDeclarativeTest extends DeclarativeTestBase<DateField> {

    private String getYearResolutionDesign() {
        return "<v-date-field resolution='year' value='2020'/>";
    }

    private DateField getYearResolutionExpected() {
        DateField df = new DateField();
        df.setResolution(Resolution.YEAR);
        df.setValue(new Date(2020 - 1900, 1 - 1, 1));
        return df;
    }

    private String getTimezoneDesign() {
        return "<v-date-field range-start=\"2014-05-05 00:00:00+0300\" range-end=\"2014-06-05 00:00:00+0300\" date-out-of-range-message=\"Please select a sensible date\" date-format=\"yyyy-MM-dd\" lenient='true' show-iso-week-numbers='true' parse-error-message=\"You are doing it wrong\" time-zone=\"GMT+05:00\" value=\"2014-05-15 00:00:00+0300\"/>";
    }

    private DateField getTimezoneExpected() {
        DateField df = new DateField();

        df.setRangeStart(new Date(2014 - 1900, 5 - 1, 5));
        df.setRangeEnd(new Date(2014 - 1900, 6 - 1, 5));
        df.setDateOutOfRangeMessage("Please select a sensible date");
        df.setResolution(Resolution.DAY);
        df.setDateFormat("yyyy-MM-dd");
        df.setLenient(true);
        df.setShowISOWeekNumbers(true);
        df.setParseErrorMessage("You are doing it wrong");
        df.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        df.setValue(new Date(2014 - 1900, 5 - 1, 15));

        return df;
    }

    @Test
    public void readTimezone() {
        testRead(getTimezoneDesign(), getTimezoneExpected());
    }

    @Test
    public void writeTimezone() {
        testWrite(getTimezoneDesign(), getTimezoneExpected());
    }

    @Test
    public void readYearResolution() {
        testRead(getYearResolutionDesign(), getYearResolutionExpected());
    }

    @Test
    public void writeYearResolution() {
        // Writing is always done in full resolution..
        testWrite(
                getYearResolutionDesign().replace("2020",
                        "2020-01-01 00:00:00+0200"),
                getYearResolutionExpected());
    }
}
