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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.declarative.Design;

/**
 * Tests the declarative support for implementations of {@link DateField}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class InlineDateFieldDeclarativeTest extends
        DeclarativeTestBase<InlineDateField> {

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

}
