package com.vaadin.tests.data.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.data.validator.RegexpValidator;

public class RegexpValidatorTest {

    private RegexpValidator completeValidator = new RegexpValidator("pattern",
            true, "Complete match validator error");
    private RegexpValidator partialValidator = new RegexpValidator("pattern",
            false, "Partial match validator error");

    @Test
    public void testRegexpValidatorWithNull() {
        assertTrue(completeValidator.isValid(null));
        assertTrue(partialValidator.isValid(null));
    }

    @Test
    public void testRegexpValidatorWithEmptyString() {
        assertTrue(completeValidator.isValid(""));
        assertTrue(partialValidator.isValid(""));
    }

    @Test
    public void testCompleteRegexpValidatorWithFaultyString() {
        assertFalse(completeValidator.isValid("mismatch"));
        assertFalse(completeValidator.isValid("pattern2"));
        assertFalse(completeValidator.isValid("1pattern"));
    }

    @Test
    public void testCompleteRegexpValidatorWithOkString() {
        assertTrue(completeValidator.isValid("pattern"));
    }

    @Test
    public void testPartialRegexpValidatorWithFaultyString() {
        assertFalse(partialValidator.isValid("mismatch"));
    }

    @Test
    public void testPartialRegexpValidatorWithOkString() {
        assertTrue(partialValidator.isValid("pattern"));
        assertTrue(partialValidator.isValid("1pattern"));
        assertTrue(partialValidator.isValid("pattern2"));
        assertTrue(partialValidator.isValid("1pattern2"));
    }
}
