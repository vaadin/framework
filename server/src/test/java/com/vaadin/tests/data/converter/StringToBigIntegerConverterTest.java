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

import java.math.BigInteger;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToBigIntegerConverter;

public class StringToBigIntegerConverterTest
        extends AbstractStringConverterTest {

    @Override
    protected StringToBigIntegerConverter getConverter() {
        return new StringToBigIntegerConverter(getErrorMessage());
    }

    @Test
    public void testValueParsing() {
        String bigInt = "1180591620717411303424"; // 2^70 > 2^63 - 1
        Result<BigInteger> converted = getConverter().convertToModel(bigInt,
                new ValueContext());
        BigInteger expected = new BigInteger(bigInt);
        assertValue("Value bigger than max long was converted incorrectly",
                expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigInteger bd = new BigInteger("1000");
        String expected = "1.000";

        String converted = getConverter().convertToPresentation(bd,
                new ValueContext(Locale.GERMAN));
        Assert.assertEquals(
                "Value with specific locale was converted incorrectly",
                expected, converted);
    }

    @Test
    public void customEmptyValue() {
        StringToBigIntegerConverter converter = new StringToBigIntegerConverter(
                BigInteger.ZERO, getErrorMessage());

        assertValue(BigInteger.ZERO,
                converter.convertToModel("", new ValueContext()));
        Assert.assertEquals("0", converter
                .convertToPresentation(BigInteger.ZERO, new ValueContext()));
    }

}
