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
package com.vaadin.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateFieldTestCase {

    private AbstractLocalDateField dateField;
    private LocalDate date;

    @Before
    public void setup() {
        dateField = new AbstractLocalDateField() {
        };
        date = LocalDate.now();
    }

    @Test
    public void rangeStartIsSetToNull() {
        dateField.setRangeStart(null);

        assertThat(dateField.getRangeStart(), is(nullValue()));
    }

    @Test
    public void rangeStartIsAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date);
        Assert.assertNull(dateField.getComponentError());
    }

    @Test
    public void belowRangeStartIsNotAcceptedAsValue() {
        dateField.setRangeStart(date);
        dateField.setValue(date.minusDays(1));
        Assert.assertNotNull(dateField.getComponentError());
    }

    @Test
    public void rangeEndIsSetToNull() {
        dateField.setRangeEnd(null);

        assertThat(dateField.getRangeEnd(), is(nullValue()));
    }

    @Test
    public void rangeEndIsAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date);
        Assert.assertNull(dateField.getComponentError());
    }

    @Test
    public void aboveRangeEndIsNotAcceptedAsValue() {
        dateField.setRangeEnd(date);
        dateField.setValue(date.plusDays(1));
        Assert.assertNotNull(dateField.getComponentError());
    }
}
