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
package com.vaadin.tests.data.converter;

import java.math.BigDecimal;
import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.StringToBigDecimalConverter;

public class StringToBigDecimalConverterTest extends TestCase {

    StringToBigDecimalConverter converter = new StringToBigDecimalConverter();

    public void testNullConversion() {
        assertEquals(null,
                converter.convertToModel(null, BigDecimal.class, null));
    }

    public void testEmptyStringConversion() {
        assertEquals(null, converter.convertToModel("", BigDecimal.class, null));
    }

    public void testValueParsing() {
        BigDecimal converted = converter.convertToModel("10", BigDecimal.class,
                null);
        BigDecimal expected = new BigDecimal(10);
        assertEquals(expected, converted);
    }

    public void testValueFormatting() {
        BigDecimal bd = new BigDecimal(12.5);
        String expected = "12,5";

        String converted = converter.convertToPresentation(bd, String.class,
                Locale.GERMAN);
        assertEquals(expected, converted);
    }
}
