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
package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldTimeTest extends MultiBrowserTest {

    private static TimeZone defaultTimeZone;

    @BeforeClass
    public static void init() {
        defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Helsinki"));
    }

    @AfterClass
    public static void cleanup() {
        TimeZone.setDefault(defaultTimeZone);
    }

    @Test
    public void time() {
        openTestURL();

        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        dateField.setValue("12/5/25 09:52 AM");
        assertEquals("12/5/25 09:52 AM", dateField.getValue());
    }
}
