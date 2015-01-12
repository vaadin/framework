package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.Validator;
import com.vaadin.data.validator.CompositeValidator;
import com.vaadin.data.validator.CompositeValidator.CombinationMode;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;

public class CompositeValidatorTest extends TestCase {

    CompositeValidator and = new CompositeValidator(CombinationMode.AND,
            "One validator not valid");
    CompositeValidator or = new CompositeValidator(CombinationMode.OR,
            "No validators are valid");
    EmailValidator email = new EmailValidator("Faulty email");
    RegexpValidator regex = new RegexpValidator("@mail.com", false,
            "Partial match validator error");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        and.addValidator(email);
        and.addValidator(regex);

        or.addValidator(email);
        or.addValidator(regex);
    }

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
