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

import java.math.BigInteger;
import java.util.Locale;

import junit.framework.TestCase;

import com.vaadin.data.util.converter.StringToBigIntegerConverter;

public class StringToBigIntegerConverterTest extends TestCase {

    StringToBigIntegerConverter converter = new StringToBigIntegerConverter();

    public void testNullConversion() {
        assertEquals("Null value was converted incorrectly", null,
                converter.convertToModel(null, BigInteger.class, null));
    }

    public void testEmptyStringConversion() {
        assertEquals("Empty value was converted incorrectly", null,
                converter.convertToModel("", BigInteger.class, null));
    }

    public void testValueParsing() {
        String bigInt = "1180591620717411303424"; // 2^70 > 2^63 - 1
        BigInteger converted = converter.convertToModel(bigInt,
                BigInteger.class, null);
        BigInteger expected = new BigInteger(bigInt);
        assertEquals("Value bigger than max long was converted incorrectly",
                expected, converted);
    }

    public void testValueFormatting() {
        BigInteger bd = new BigInteger("1000");
        String expected = "1.000";

        String converted = converter.convertToPresentation(bd, String.class,
                Locale.GERMAN);
        assertEquals("Value with specific locale was converted incorrectly",
                expected, converted);
    }
}
