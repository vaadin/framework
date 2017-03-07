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

import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.StringToIntegerConverter;

public class StringToIntegerConverterTest {

    StringToIntegerConverter converter = new StringToIntegerConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, Integer.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null,
                converter.convertToModel("", Integer.class, null));
    }

    @Test
    public void testValueOutOfRange() {
        Double[] values = new Double[] { Integer.MAX_VALUE * 2.0,
                Integer.MIN_VALUE * 2.0, Long.MAX_VALUE * 2.0,
                Long.MIN_VALUE * 2.0 };

        boolean accepted = false;
        for (Number value : values) {
            try {
                converter.convertToModel(String.format("%.0f", value),
                        Integer.class, null);
                accepted = true;
            } catch (ConversionException expected) {
            }
        }
        Assert.assertFalse("Accepted value outside range of int", accepted);
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals(Integer.valueOf(10),
                converter.convertToModel("10", Integer.class, null));
    }
}
