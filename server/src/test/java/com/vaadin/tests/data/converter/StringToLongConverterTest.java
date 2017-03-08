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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToLongConverter;

public class StringToLongConverterTest extends AbstractStringConverterTest {

    @Override
    protected StringToLongConverter getConverter() {
        return new StringToLongConverter(getErrorMessage());
    }

    @Override
    @Test
    public void testEmptyStringConversion() {
        assertValue(null,
                getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testValueConversion() {
        assertValue(Long.valueOf(10),
                getConverter().convertToModel("10", new ValueContext()));
    }

    @Test
    public void testExtremeLongValueConversion() {
        Result<Long> l = getConverter().convertToModel("9223372036854775807",
                new ValueContext());
        assertValue(Long.MAX_VALUE, l);
        l = getConverter().convertToModel("-9223372036854775808",
                new ValueContext());
        assertValue(Long.MIN_VALUE, l);
    }

    @Test
    public void testOutOfBoundsValueConversion() {
        // Long.MAX_VALUE+1 is converted to Long.MAX_VALUE
        Result<Long> l = getConverter().convertToModel("9223372036854775808",
                new ValueContext());
        assertValue(Long.MAX_VALUE, l);
        // Long.MIN_VALUE-1 is converted to Long.MIN_VALUE
        l = getConverter().convertToModel("-9223372036854775809",
                new ValueContext());
        assertValue(Long.MIN_VALUE, l);

    }

    @Test
    public void customEmptyValue() {
        StringToLongConverter converter = new StringToLongConverter((long) 0,
                getErrorMessage());

        assertValue((long) 0, converter.convertToModel("", new ValueContext()));
        Assert.assertEquals("0",
                converter.convertToPresentation((long) 0, new ValueContext()));
    }
}
