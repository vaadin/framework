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

import com.vaadin.v7.data.util.converter.StringToFloatConverter;

public class StringToFloatConverterTest {

    StringToFloatConverter converter = new StringToFloatConverter();

    @Test
    public void testNullConversion() {
        Assert.assertEquals(null,
                converter.convertToModel(null, Float.class, null));
    }

    @Test
    public void testEmptyStringConversion() {
        Assert.assertEquals(null,
                converter.convertToModel("", Float.class, null));
    }

    @Test
    public void testValueConversion() {
        Assert.assertEquals(Float.valueOf(10),
                converter.convertToModel("10", Float.class, null));
    }
}
