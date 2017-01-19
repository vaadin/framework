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
package com.vaadin.tests.server.component.datefield;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateTimeFieldDeclarativeTest;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.InlineDateTimeField;
import com.vaadin.ui.declarative.Design;

/**
 * Tests the declarative support for implementations of
 * {@link AbstractDateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class InlineDateTimeFieldDeclarativeTest
        extends AbstractLocalDateTimeFieldDeclarativeTest<InlineDateTimeField> {

    @Test
    public void testInlineDateFieldToFromDesign() throws Exception {
        InlineDateTimeField field = new InlineDateTimeField("Day is",
                LocalDateTime.of(2003, 2, 27, 4, 13, 45));
        field.setShowISOWeekNumbers(true);
        field.setRangeStart(LocalDateTime.of(2001, 2, 27, 6, 12, 53));
        field.setRangeEnd(LocalDateTime.of(20011, 2, 27, 3, 43, 23));
        field.setResolution(DateTimeResolution.SECOND);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Design.write(field, bos);

        InlineDateTimeField result = (InlineDateTimeField) Design
                .read(new ByteArrayInputStream(bos.toByteArray()));
        assertEquals(field.getResolution(), result.getResolution());
        assertEquals(field.getCaption(), result.getCaption());
        assertEquals(field.getValue(), result.getValue());
        assertEquals(field.getRangeStart(), result.getRangeStart());
        assertEquals(field.getRangeEnd(), result.getRangeEnd());
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-inline-date-time-field";
    }

    @Override
    protected Class<? extends InlineDateTimeField> getComponentClass() {
        return InlineDateTimeField.class;
    }

}
