package com.vaadin.data.validator;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;

import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.ValueContext;
import com.vaadin.ui.Label;

public class ValidatorTestBase {

    private Label localeContext;

    @Before
    public void setUp() {
        localeContext = new Label();
    }

    protected <T> void assertPasses(T value, Validator<? super T> v) {
        v.apply(value, new ValueContext())
                .handle(val -> Assert.assertEquals(value, val), err -> Assert
                        .fail(value + " should pass " + v + " but got " + err));
    }

    protected <T> void assertFails(T value, String errorMessage,
            Validator<? super T> v) {
        v.apply(value, new ValueContext(localeContext)).handle(
                val -> Assert.fail(value + " should fail " + v),
                err -> Assert.assertEquals(errorMessage, err));
    }

    protected <T> void assertFails(T value, AbstractValidator<? super T> v) {
        assertFails(value, v.getMessage(value), v);
    }

    protected void setLocale(Locale locale) {
        localeContext.setLocale(locale);
    }
}