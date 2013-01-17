package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.NullValidator;

public class TestNullValidator extends TestCase {

    NullValidator notNull = new NullValidator("Null not accepted", false);
    NullValidator onlyNull = new NullValidator("Only null accepted", true);

    public void testNullValue() {
        try {
            notNull.validate(null);
            fail("expected null to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Null not accepted", ex.getMessage());
        }
        try {
            onlyNull.validate(null);
        } catch (Validator.InvalidValueException ex) {
            fail("onlyNull should not throw exception for null");
        }
    }

    public void testNonNullValue() {
        try {
            onlyNull.validate("Not a null value");
            fail("expected onlyNull validator to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Only null accepted", ex.getMessage());
        }
        try {
            notNull.validate("Not a null value");
        } catch (Validator.InvalidValueException ex) {
            fail("notNull should not throw exception for \"Not a null value\"");
        }
    }
}
