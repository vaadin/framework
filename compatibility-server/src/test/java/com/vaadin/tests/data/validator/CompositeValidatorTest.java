package com.vaadin.tests.data.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.LegacyCompositeValidator;
import com.vaadin.v7.data.validator.LegacyCompositeValidator.CombinationMode;
import com.vaadin.v7.data.validator.LegacyEmailValidator;
import com.vaadin.v7.data.validator.LegacyRegexpValidator;

public class CompositeValidatorTest {

    LegacyCompositeValidator and = new LegacyCompositeValidator(
            CombinationMode.AND, "One validator not valid");
    LegacyCompositeValidator or = new LegacyCompositeValidator(
            CombinationMode.OR, "No validators are valid");
    LegacyEmailValidator email = new LegacyEmailValidator("Faulty email");
    LegacyRegexpValidator regex = new LegacyRegexpValidator("@mail.com", false,
            "Partial match validator error");

    @Before
    public void setUp() {
        and.addValidator(email);
        and.addValidator(regex);

        or.addValidator(email);
        or.addValidator(regex);
    }

    @Test
    public void testCorrectValue() {
        String testString = "user@mail.com";
        assertTrue(email.isValid(testString));
        assertTrue(regex.isValid(testString));
        try {
            // notNull.validate(null);
            // fail("expected null to fail with an exception");
            and.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            fail("And validator should be valid");
        }
        try {
            or.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            fail("And validator should be valid");
        }
    }

    @Test
    public void testCorrectRegex() {

        String testString = "@mail.com";
        assertFalse(testString + " should not validate",
                email.isValid(testString));
        assertTrue(testString + "should validate", regex.isValid(testString));
        try {
            // notNull.validate(null);
            and.validate(testString);
            fail("expected and to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Faulty email", ex.getMessage());
            // fail("And validator should be valid");
        }
        try {
            or.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            // assertEquals("Null not accepted", ex.getMessage());
            fail("Or validator should be valid");
        }
    }

    @Test
    public void testCorrectEmail() {

        String testString = "user@gmail.com";

        assertTrue(testString + " should validate", email.isValid(testString));
        assertFalse(testString + " should not validate",
                regex.isValid(testString));
        try {
            and.validate(testString);
            fail("expected and to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Partial match validator error", ex.getMessage());
        }
        try {
            or.validate(testString);
        } catch (Validator.InvalidValueException ex) {
            fail("Or validator should be valid");
        }
    }

    @Test
    public void testBothFaulty() {

        String testString = "gmail.com";

        assertFalse(testString + " should not validate",
                email.isValid(testString));
        assertFalse(testString + " should not validate",
                regex.isValid(testString));
        try {
            and.validate(testString);
            fail("expected and to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("Faulty email", ex.getMessage());
        }
        try {
            or.validate(testString);
            fail("expected or to fail with an exception");
        } catch (Validator.InvalidValueException ex) {
            assertEquals("No validators are valid", ex.getMessage());
        }
    }

}
