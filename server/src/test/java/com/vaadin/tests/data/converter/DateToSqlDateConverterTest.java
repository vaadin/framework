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
package com.vaadin.tests.data.converter;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.DateToSqlDateConverter;

public class DateToSqlDateConverterTest extends AbstractConverterTest {

    @Override
    protected DateToSqlDateConverter getConverter() {
        return new DateToSqlDateConverter();
    }

    @Test
    public void testValueConversion() {
        Date testDate = new Date(100, 0, 1);
        long time = testDate.getTime();
        assertValue(testDate, getConverter().convertToModel(
                new java.sql.Date(time), new ValueContext(Locale.ENGLISH)));
    }
}
