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
package com.vaadin.data.validator;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;

/**
 * @author Vaadin Ltd
 *
 */
public class NotEmptyValidatorTest {

    @Test
    public void nullValueIsDisallowed() {
        NotEmptyValidator<String> validator = new NotEmptyValidator<>("foo");
        Result<String> result = validator.apply(null);
        Assert.assertTrue(result.isError());
        Assert.assertEquals("foo", result.getMessage().get());
    }

    @Test
    public void emptyValueIsDisallowed() {
        NotEmptyValidator<String> validator = new NotEmptyValidator<>("foo");
        Result<String> result = validator.apply("");
        Assert.assertTrue(result.isError());
        Assert.assertEquals("foo", result.getMessage().get());
    }

    @Test
    public void nonNullValueIsAllowed() {
        NotEmptyValidator<Object> validator = new NotEmptyValidator<>("foo");
        Object value = new Object();
        Result<Object> result = validator.apply(value);
        Assert.assertFalse(result.isError());
        result.ifOk(val -> Assert.assertEquals(value, val));
        result.ifError(msg -> Assert.fail());
    }
}
