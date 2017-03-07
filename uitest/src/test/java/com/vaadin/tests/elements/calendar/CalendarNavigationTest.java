/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.elements.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CalendarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CalendarNavigationTest extends MultiBrowserTest {

    private CalendarElement calendarElement;

    @Override
    protected Class<?> getUIClass() {
        return CalendarUI.class;
    }

    @Before
    public void init() {
        openTestURL();
        calendarElement = $(CalendarElement.class).first();
    }

    @Test
    public void calendarNavigation_backAndForwardInWeekView_navigationWorks() {
        assertTrue(calendarElement.hasWeekView());
        String originalFirstDay = calendarElement.getDayHeaders().get(0)
                .getText();

        calendarElement.back();
        calendarElement.waitForVaadin();
        assertNotEquals(originalFirstDay,
                calendarElement.getDayHeaders().get(0).getText());

        calendarElement.next();
        calendarElement.waitForVaadin();

        assertEquals(originalFirstDay,
                calendarElement.getDayHeaders().get(0).getText());
    }

    @Test(expected = IllegalStateException.class)
    public void calendarNavigation_navigationInMonthView_exceptionThrown() {
        $(ButtonElement.class).get(0).click();
        calendarElement.waitForVaadin();

        assertTrue(calendarElement.hasMonthView());

        calendarElement.next();
    }
}
