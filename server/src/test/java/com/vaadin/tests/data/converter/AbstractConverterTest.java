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

public abstract class AbstractConverterTest {

    @Test
    public void testNullConversion() {
        assertValue(null,
                getConverter().convertToModel(null, new ValueContext()));
    }

    protected abstract Converter<?, ?> getConverter();

    protected <T> void assertValue(T expectedValue, Result<?> result) {
        assertValue(null, expectedValue, result);
    }

    protected <T> void assertValue(String assertMessage, T expectedValue,
            Result<?> result) {
        Assert.assertNotNull("Result should never be null", result);
        Assert.assertFalse("Result is not ok", result.isError());
        Assert.assertEquals(expectedValue,
                result.getOrThrow(message -> new AssertionError(
                        assertMessage != null ? assertMessage : message)));
    }

    protected void assertError(String expectedResultMessage, Result<?> result) {
        Assert.assertNotNull("Result should never be null", result);
        Assert.assertTrue("Result should be an error", result.isError());
        Assert.assertEquals(expectedResultMessage, result.getMessage().get());
    }

    protected String getErrorMessage() {
        return "conversion failed";
    }

}
