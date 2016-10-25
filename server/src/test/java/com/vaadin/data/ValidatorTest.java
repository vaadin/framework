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
package com.vaadin.data;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.util.converter.ValueContext;

/**
 * @author Vaadin Ltd
 *
 */
public class ValidatorTest {

    @Test
    public void alwaysPass() {
        Validator<String> alwaysPass = Validator.alwaysPass();
        Result<String> result = alwaysPass.apply("foo", new ValueContext());
        Assert.assertTrue(result instanceof SimpleResult);
        SimpleResult<String> implRes = (SimpleResult<String>) result;
        Assert.assertFalse(implRes.getMessage().isPresent());
    }

    @Test
    public void from() {
        Validator<String> validator = Validator.from(Objects::nonNull,
                "Cannot be null");
        Result<String> result = validator.apply(null, new ValueContext());
        Assert.assertTrue(result.isError());

        result = validator.apply("", new ValueContext());
        Assert.assertFalse(result.isError());
    }
}
