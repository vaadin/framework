package com.vaadin.data.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;

import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.Label;

public class ValidatorTestBase {

    private Label localeContext;

    @Before
    public void setUp() {
        localeContext = new Label();
        setLocale(Locale.US);
    }

    protected <T> void assertPasses(T value, Validator<? super T> validator) {
        ValidationResult result = validator.apply(value, new ValueContext());
        if (result.isError()) {
            fail(value + " should pass " + validator + " but got "
                    + result.getErrorMessage());
        }
    }

    protected <T> void assertFails(T value, String errorMessage,
            Validator<? super T> validator) {
        ValidationResult result = validator.apply(value,
                new ValueContext(localeContext));
        assertTrue(result.isError());
        assertEquals(errorMessage, result.getErrorMessage());
    }

    protected <T> void assertFails(T value, AbstractValidator<? super T> v) {
        assertFails(value, v.getMessage(value), v);
    }

    protected void setLocale(Locale locale) {
        localeContext.setLocale(locale);
    }
}
