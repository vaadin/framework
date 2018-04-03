package com.vaadin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        assertFalse(ok.isError());
        assertFalse(ok.getMessage().isPresent());
        ok.ifOk(v -> assertEquals(value, v));
        ok.ifError(msg -> fail());
    }

    @Test
    public void testError() {
        String message = "foo";
        Result<String> error = Result.error(message);
        assertTrue(error.isError());
        assertTrue(error.getMessage().isPresent());
        error.ifOk(v -> fail());
        error.ifError(msg -> assertEquals(message, msg));
        assertEquals(message, error.getMessage().get());
    }

    @Test
    public void of_noException() {
        Result<String> result = Result.of(() -> "", exception -> null);
        assertTrue(result instanceof SimpleResult);
        assertFalse(result.isError());
    }

    @Test
    public void of_exception() {
        String message = "foo";
        Result<String> result = Result.of(() -> {
            throw new RuntimeException();
        }, exception -> message);
        assertTrue(result instanceof SimpleResult);
        assertTrue(result.isError());
        assertEquals(message, result.getMessage().get());
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
            assertEquals("foo", value);
            return "bar";
        });
        assertTrue(mapResult instanceof SimpleResult);
        assertFalse(mapResult.isError());
        mapResult.ifOk(v -> assertEquals("bar", v));
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
            assertEquals("foo", value);
            return "somevalue";
        });
        assertTrue(mapResult instanceof SimpleResult);
        assertTrue(mapResult.isError());
        mapResult.ifError(msg -> assertEquals("bar", msg));
    }
}
