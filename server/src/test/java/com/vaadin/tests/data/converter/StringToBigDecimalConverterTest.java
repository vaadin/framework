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

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.StringToBigDecimalConverter;

public class StringToBigDecimalConverterTest
        extends AbstractStringConverterTest {

    @Override
    protected StringToBigDecimalConverter getConverter() {
        return new StringToBigDecimalConverter(getErrorMessage());
    }

    @Test
    public void testValueParsing() {
        Result<BigDecimal> converted = getConverter().convertToModel("10",
                null);
        BigDecimal expected = new BigDecimal(10);
        assertValue(expected, converted);
    }

    @Test
    public void testValueFormatting() {
        BigDecimal bd = new BigDecimal(12.5);
        String expected = "12,5";

        String converted = getConverter().convertToPresentation(bd,
                Locale.GERMAN);
        Assert.assertEquals(expected, converted);
    }
}
