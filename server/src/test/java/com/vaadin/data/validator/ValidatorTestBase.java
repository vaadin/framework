package com.vaadin.data.validator;

import org.junit.Assert;

import com.vaadin.data.Validator;

public class ValidatorTestBase {

    protected <T> void assertPasses(T value, Validator<? super T> v) {
        v.apply(value).handle(val -> Assert.assertEquals(value, val),
                err -> Assert
                        .fail(value + " should pass " + v + " but got " + err));
    }

    protected <T> void assertFails(T value, String errorMessage,
            Validator<? super T> v) {
        v.apply(value).handle(val -> Assert.fail(value + " should fail " + v),
                err -> Assert.assertEquals(errorMessage, err));
    }

    protected <T> void assertFails(T value, AbstractValidator<? super T> v) {
        assertFails(value, v.getMessage(value), v);
    }
}
