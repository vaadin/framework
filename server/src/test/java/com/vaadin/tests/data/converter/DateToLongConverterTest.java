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

import org.junit.Test;

import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.DateToLongConverter;

public class DateToLongConverterTest extends AbstractConverterTest {

    @Override
    protected DateToLongConverter getConverter() {
        return new DateToLongConverter();
    }

    @Override
    @Test
    public void testNullConversion() {
        assertValue(null,
                getConverter().convertToModel(null, new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        Date d = new Date(100, 0, 1);
        assertValue(
                Long.valueOf(946677600000l
                        + (d.getTimezoneOffset() + 120) * 60 * 1000L),
                getConverter().convertToModel(d, new ValueContext()));
    }
}
