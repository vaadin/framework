package com.vaadin.tokka.data.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.vaadin.tokka.data.Validator;

public class ValidatorTestBase {

    protected <T> void assertPasses(T value, Validator<? super T> v) {
        v.apply(value).handle(
                val -> assertEquals(value, val),
                err -> fail(value + " should pass " + v + " but got " + err));
    }

    protected <T> void assertFails(T value, String errorMessage,
            Validator<? super T> v) {
        v.apply(value).handle(
                val -> fail(value + " should fail " + v),
                err -> assertEquals(errorMessage, err));
    }

    protected <T> void assertFails(T value, AbstractValidator<? super T> v) {
        assertFails(value, v.getMessage(value), v);
    }
}
