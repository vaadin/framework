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

import java.time.LocalDate;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.DateField;

/**
 * Tests the declarative support for implementations of
 * {@link AbstractDateField}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class DateFieldDeclarativeTest extends DeclarativeTestBase<DateField> {

    private String getBasicDesign() {
        return "<vaadin-date-field assistive-text='at' text-field-enabled='false' show-iso-week-numbers range-end=\"2019-01-15\" input-prompt=\"Pick a day\" value=\"2003-02-27\"></vaadin-date-field>";
    }

    private DateField getBasicExpected() {
        DateField pdf = new DateField();
        pdf.setShowISOWeekNumbers(true);
        pdf.setRangeEnd(LocalDate.of(2019, 01, 15));
        pdf.setInputPrompt("Pick a day");
        pdf.setValue(LocalDate.of(2003, 2, 27));
        pdf.setTextFieldEnabled(false);
        pdf.setAssistiveText("at");
        return pdf;
    }

    @Test
    public void readBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void writeBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

}
