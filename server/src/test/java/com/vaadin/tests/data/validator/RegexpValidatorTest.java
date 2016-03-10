package com.vaadin.tests.data.validator;

import junit.framework.TestCase;

import com.vaadin.data.validator.RegexpValidator;

public class RegexpValidatorTest extends TestCase {

    private RegexpValidator completeValidator = new RegexpValidator("pattern",
            true, "Complete match validator error");
    private RegexpValidator partialValidator = new RegexpValidator("pattern",
            false, "Partial match validator error");

    public void testRegexpValidatorWithNull() {
        assertTrue(completeValidator.isValid(null));
        assertTrue(partialValidator.isValid(null));
    }

    public void testRegexpValidatorWithEmptyString() {
        assertTrue(completeValidator.isValid(""));
        assertTrue(partialValidator.isValid(""));
    }

    public void testCompleteRegexpValidatorWithFaultyString() {
        assertFalse(completeValidator.isValid("mismatch"));
        assertFalse(completeValidator.isValid("pattern2"));
        assertFalse(completeValidator.isValid("1pattern"));
    }

    public void testCompleteRegexpValidatorWithOkString() {
        assertTrue(completeValidator.isValid("pattern"));
    }

    public void testPartialRegexpValidatorWithFaultyString() {
        assertFalse(partialValidator.isValid("mismatch"));
    }

    public void testPartialRegexpValidatorWithOkString() {
        assertTrue(partialValidator.isValid("pattern"));
        assertTrue(partialValidator.isValid("1pattern"));
        assertTrue(partialValidator.isValid("pattern2"));
        assertTrue(partialValidator.isValid("1pattern2"));
    }
}
