/*
 * Copyright 2000-2021 Vaadin Ltd.
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

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.AbstractDateFieldServerRpc;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.server.component.abstractdatefield.AbstractLocalDateFieldDeclarativeTest;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.InlineDateField;

/**
 * Tests the resetting of component error after setting empty date string in
 * {@link AbstractDateField}.
 */
public class DateFieldErrorMessageTest
        extends AbstractLocalDateFieldDeclarativeTest<InlineDateField> {

    @Test
    public void testErrorMessageRemoved() throws Exception {
        InlineDateField field = new InlineDateField("Day is",
                LocalDate.of(2003, 2, 27));
        checkValueAndComponentError(field, "2003-02-27",
                LocalDate.of(2003, 2, 27), false);
        checkValueAndComponentError(field, "", null, false);
        checkValueAndComponentError(field, "2003-04-27",
                LocalDate.of(2003, 4, 27), false);
        checkValueAndComponentError(field, "foo", null, true);
        checkValueAndComponentError(field, "2013-07-03",
                LocalDate.of(2013, 7, 3), false);
        checkValueAndComponentError(field, "foo", null, true);
        checkValueAndComponentError(field, "", null, false);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-inline-date-field";
    }

    @Override
    protected Class<? extends InlineDateField> getComponentClass() {
        return InlineDateField.class;
    }

    private void checkValueAndComponentError(InlineDateField field,
            String newInput, LocalDate expectedFieldValue,
            boolean componentErrorExpected) throws Exception {
        setDateByText(field, newInput);
        assertEquals(expectedFieldValue, field.getValue());
        assertEquals(componentErrorExpected, field.getComponentError() != null);
    }

    private void setDateByText(InlineDateField field, String dateText)
            throws Exception {
        Field rcpField = AbstractDateField.class.getDeclaredField("rpc");
        rcpField.setAccessible(true);
        AbstractDateFieldServerRpc rcp = (AbstractDateFieldServerRpc) rcpField
                .get(field);
        Map<String, Integer> resolutions = new HashMap<String, Integer>();
        try {
            LocalDate date = LocalDate.parse(dateText);
            resolutions.put(DateResolution.YEAR.name(), date.getYear());
            resolutions.put(DateResolution.MONTH.name(), date.getMonthValue());
            resolutions.put(DateResolution.DAY.name(), date.getDayOfMonth());
        } catch (Exception e) {
            // ignore
        }
        rcp.update(dateText, resolutions);
    }
}
