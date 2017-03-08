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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.ReverseConverter;
import com.vaadin.v7.data.util.converter.StringToByteConverter;

public class StringToByteConverterTest {

    StringToByteConverter converter = new StringToByteConverter();
    Converter<Byte, String> reverseConverter = new ReverseConverter<Byte, String>(
            converter);

    @Test
    public void testNullConversion() {
        Assert.assertEquals("Null value was converted incorrectly", null,
                converter.convertToModel(null, Byte.class, null));
    }

    @Test
    public void testReverseNullConversion() {
        Assert.assertEquals("Null value reversely was converted incorrectly",
                null,
                reverseConverter.convertToModel(null, String.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals("Empty value was converted incorrectly", null,
                converter.convertToModel("", Byte.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals("Byte value was converted incorrectly",
                Byte.valueOf((byte) 10),
                converter.convertToModel("10", Byte.class, null));
    }

    @Test
    public void testReverseValueConversion() {
        Assert.assertEquals("Byte value reversely was converted incorrectly",
                reverseConverter.convertToModel((byte) 10, String.class, null),
                "10");
    }

    @Test
    public void testExtremeByteValueConversion() {
        byte b = converter.convertToModel("127", Byte.class, null);
        Assert.assertEquals(Byte.MAX_VALUE, b);
        b = converter.convertToModel("-128", Byte.class, null);
        Assert.assertEquals("Min byte value was converted incorrectly",
                Byte.MIN_VALUE, b);
    }

    @Test
    public void testValueOutOfRange() {
        Double[] values = new Double[] { Byte.MAX_VALUE * 2.0,
                Byte.MIN_VALUE * 2.0, Long.MAX_VALUE * 2.0,
                Long.MIN_VALUE * 2.0 };

        boolean accepted = false;
        for (Number value : values) {
            try {
                converter.convertToModel(String.format("%.0f", value),
                        Byte.class, null);
                accepted = true;
            } catch (ConversionException expected) {
            }
        }
        Assert.assertFalse("Accepted value outside range of int", accepted);
    }
}
