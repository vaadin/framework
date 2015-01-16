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
package com.vaadin.tests.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.declarative.Design;

/**
 * Tests the declarative support for implementations of {@link DateField}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DateFieldsTest {

    @Test
    public void testInlineDateFieldToFromDesign() throws Exception {
        InlineDateField field = new InlineDateField("Day is",
                new SimpleDateFormat("yyyy-MM-dd").parse("2003-02-27"));
        field.setResolution(Resolution.DAY);
        field.setShowISOWeekNumbers(true);
        field.setRangeStart(new SimpleDateFormat("yyyy-MM-dd")
                .parse("2001-02-27"));
        field.setRangeEnd(new SimpleDateFormat("yyyy-MM-dd")
                .parse("2011-02-27"));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(field, bos);

        InlineDateField result = (InlineDateField) Design
                .read(new ByteArrayInputStream(bos.toByteArray()));
        assertEquals(field.getResolution(), result.getResolution());
        assertEquals(field.getCaption(), result.getCaption());
        assertEquals(field.getValue(), result.getValue());
        assertEquals(field.getRangeStart(), result.getRangeStart());
        assertEquals(field.getRangeEnd(), result.getRangeEnd());
    }

    @Test
    public void testPopupDateFieldFromDesign() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(
                "<!DOCTYPE html><html><head></head><body><v-popup-date-field show-iso-week-numbers caption=\"Day is\" resolution=\"MINUTE\" range-end=\"2019-01-15\" input-prompt=\"Pick a day\" value=\"2003-02-27 07:15\"></v-popup-date-field></body></html>"
                        .getBytes());
        PopupDateField result = (PopupDateField) Design.read(bis);
        assertEquals(Resolution.MINUTE, result.getResolution());
        assertEquals("Day is", result.getCaption());
        assertTrue(result.isShowISOWeekNumbers());
        assertEquals("Pick a day", result.getInputPrompt());
        assertEquals(
                new SimpleDateFormat("yyyy-MM-dd HH:mm")
                        .parse("2003-02-27 07:15"),
                result.getValue());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-15"),
                result.getRangeEnd());

    }

    @Test
    public void testPopupDateFieldFromDesignInTicket() throws Exception {
        ByteArrayInputStream bis = new ByteArrayInputStream(
                "<!DOCTYPE html><html><head></head><body><v-date-field range-start=\"2014-05-05\" range-end=\"2014-06-05\" date-out-of-range-message=\"Please select a sensible date\" resolution=\"day\" date-format=\"yyyy-MM-dd\" lenient show-iso-week-numbers parse-error-message=\"You are doing it wrong\" time-zone=\"GMT+5\" value=\"2014-05-15\"></v-date-field></body></html>"
                        .getBytes());
        DateField result = (DateField) Design.read(bis);
        assertEquals(Resolution.DAY, result.getResolution());
        assertTrue(result.isShowISOWeekNumbers());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2014-05-15"),
                result.getValue());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2014-06-05"),
                result.getRangeEnd());
        assertEquals(TimeZone.getTimeZone("GMT+5"), result.getTimeZone());
    }

}
