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

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

public abstract class AbstractStringConverterTest
        extends AbstractConverterTest {

    @Override
    protected abstract Converter<String, ?> getConverter();

    @Test
    public void testEmptyStringConversion() {
        assertValue("Null value was converted incorrectly", null,
                getConverter().convertToModel("", new ValueContext()));
    }

    @Test
    public void testErrorMessage() {
        Result<?> result = getConverter().convertToModel("abc",
                new ValueContext());
        Assert.assertTrue(result.isError());
        Assert.assertEquals(getErrorMessage(), result.getMessage().get());
    }

    @Override
    protected String getErrorMessage() {
        return "conversion failed";
    }

}
