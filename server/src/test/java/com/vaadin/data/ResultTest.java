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

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.server.SerializableFunction;

/**
 * @author Vaadin Ltd
 *
 */
public class ResultTest {

    @Test
    public void testOk() {
        String value = "foo";
        Result<String> ok = Result.ok(value);
        Assert.assertFalse(ok.isError());
        Assert.assertFalse(ok.getMessage().isPresent());
        ok.ifOk(v -> Assert.assertEquals(value, v));
        ok.ifError(msg -> Assert.fail());
    }

    @Test
    public void testError() {
        String message = "foo";
        Result<String> error = Result.error(message);
        Assert.assertTrue(error.isError());
        Assert.assertTrue(error.getMessage().isPresent());
        error.ifOk(v -> Assert.fail());
        error.ifError(msg -> Assert.assertEquals(message, msg));
        Assert.assertEquals(message, error.getMessage().get());
    }

    @Test
    public void of_noException() {
        Result<String> result = Result.of(() -> "", exception -> null);
        Assert.assertTrue(result instanceof SimpleResult);
        Assert.assertFalse(result.isError());
    }

    @Test
    public void of_exception() {
        String message = "foo";
        Result<String> result = Result.of(() -> {
            throw new RuntimeException();
        }, exception -> message);
        Assert.assertTrue(result instanceof SimpleResult);
        Assert.assertTrue(result.isError());
        Assert.assertEquals(message, result.getMessage().get());
    }

    @SuppressWarnings("serial")
    @Test
    public void map_norError_mapperIsApplied() {
        Result<String> result = new SimpleResult<String>("foo", null) {

            @Override
            public <S> Result<S> flatMap(
                    SerializableFunction<String, Result<S>> mapper) {
                return mapper.apply("foo");
            }
        };
        Result<String> mapResult = result.map(value -> {
            Assert.assertEquals("foo", value);
            return "bar";
        });
        Assert.assertTrue(mapResult instanceof SimpleResult);
        Assert.assertFalse(mapResult.isError());
        mapResult.ifOk(v -> Assert.assertEquals("bar", v));
    }

    @SuppressWarnings("serial")
    @Test
    public void map_error_mapperIsApplied() {
        Result<String> result = new SimpleResult<String>("foo", null) {

            @Override
            public <S> Result<S> flatMap(
                    SerializableFunction<String, Result<S>> mapper) {
                return new SimpleResult<>(null, "bar");
            }
        };
        Result<String> mapResult = result.map(value -> {
            Assert.assertEquals("foo", value);
            return "somevalue";
        });
        Assert.assertTrue(mapResult instanceof SimpleResult);
        Assert.assertTrue(mapResult.isError());
        mapResult.ifError(msg -> Assert.assertEquals("bar", msg));
    }
}
