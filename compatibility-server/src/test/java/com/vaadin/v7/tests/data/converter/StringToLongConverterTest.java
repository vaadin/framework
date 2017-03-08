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
package com.vaadin.v7.tests.data.converter;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.ReverseConverter;
import com.vaadin.v7.data.util.converter.StringToLongConverter;

public class StringToLongConverterTest {

    StringToLongConverter converter = new StringToLongConverter();
    Converter<Long, String> reverseConverter = new ReverseConverter<Long, String>(
            converter);

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, Long.class, null));
    }

    @Test
    public void testReverseNullConversion() {
        Assert.assertEquals(null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null,
                converter.convertToModel("", Long.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals(Long.valueOf(10),
                converter.convertToModel("10", Long.class, null));
    }

    @Test
    public void testReverseValueConversion() {
        Assert.assertEquals(
                reverseConverter.convertToModel(10L, String.class, null), "10");
    }

    @Test
    public void testExtremeLongValueConversion() {
        long l = converter.convertToModel("9223372036854775807", Long.class,
                null);
        Assert.assertEquals(Long.MAX_VALUE, l);
        l = converter.convertToModel("-9223372036854775808", Long.class, null);
        Assert.assertEquals(Long.MIN_VALUE, l);
    }

    @Test
    public void testExtremeReverseLongValueConversion() {
        String str = reverseConverter.convertToModel(Long.MAX_VALUE,
                String.class, Locale.ENGLISH);
        Assert.assertEquals("9,223,372,036,854,775,807", str);
        str = reverseConverter.convertToModel(Long.MIN_VALUE, String.class,
                Locale.ENGLISH);
        Assert.assertEquals("-9,223,372,036,854,775,808", str);
    }

    @Test
    public void testOutOfBoundsValueConversion() {
        // Long.MAX_VALUE+1 is converted to Long.MAX_VALUE
        long l = converter.convertToModel("9223372036854775808", Long.class,
                null);
        Assert.assertEquals(Long.MAX_VALUE, l);
        // Long.MIN_VALUE-1 is converted to Long.MIN_VALUE
        l = converter.convertToModel("-9223372036854775809", Long.class, null);
        Assert.assertEquals(Long.MIN_VALUE, l);

    }
}
