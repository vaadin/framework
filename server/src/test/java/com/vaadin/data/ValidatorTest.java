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

/**
 * @author Vaadin Ltd
 *
 */
public class ValidatorTest {

    @Test
    public void alwaysPass() {
        Validator<String> alwaysPass = Validator.alwaysPass();
        Result<String> result = alwaysPass.apply("foo");
        Assert.assertTrue(result instanceof SimpleResult);
        SimpleResult<String> implRes = (SimpleResult<String>) result;
        Assert.assertFalse(implRes.getMessage().isPresent());
    }

    @Test
    public void chain_alwaysPassAndError() {
        Validator<String> alwaysPass = Validator.alwaysPass();
        Validator<String> chain = alwaysPass
                .chain(value -> Result.error("foo"));
        Result<String> result = chain.apply("bar");
        Assert.assertTrue(result.isError());
        Assert.assertEquals("foo", result.getMessage().get());
    }

    @SuppressWarnings("serial")
    @Test
    public void chain_mixture() {
        Validator<String> first = new Validator<String>() {

            @Override
            public Result<String> apply(String value) {
                if (value == null) {
                    return Result.error("Cannot be null");
                }
                return Result.ok(value);
            }
        };
        Validator<String> second = new Validator<String>() {

            @Override
            public Result<String> apply(String value) {
                if (value != null && value.isEmpty()) {
                    return Result.error("Cannot be empty");
                }
                return Result.ok(value);
            }
        };

        Validator<String> chain = first.chain(second);
        Result<String> result = chain.apply("bar");
        Assert.assertFalse(result.isError());

        result = chain.apply(null);
        Assert.assertTrue(result.isError());

        result = chain.apply("");
        Assert.assertTrue(result.isError());
    }

    @Test
    public void from() {
        Validator<String> validator = Validator.from(Objects::nonNull,
                "Cannot be null");
        Result<String> result = validator.apply(null);
        Assert.assertTrue(result.isError());

        result = validator.apply("");
        Assert.assertFalse(result.isError());
    }
}
