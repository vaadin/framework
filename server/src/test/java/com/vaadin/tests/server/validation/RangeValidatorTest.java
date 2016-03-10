package com.vaadin.tests.server.validation;

import junit.framework.TestCase;

import com.vaadin.data.validator.IntegerRangeValidator;

public class RangeValidatorTest extends TestCase {

    // This test uses IntegerRangeValidator for simplicity.
    // IntegerRangeValidator contains no code so we really are testing
    // RangeValidator
    public void testMinValueNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMinValueIncluded(false);
        assertFalse(iv.isValid(0));
        assertTrue(iv.isValid(10));
        assertFalse(iv.isValid(11));
        assertFalse(iv.isValid(-1));
    }

    public void testMinMaxValuesInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        assertTrue(iv.isValid(0));
        assertTrue(iv.isValid(1));
        assertTrue(iv.isValid(10));
        assertFalse(iv.isValid(11));
        assertFalse(iv.isValid(-1));
    }

    public void testMaxValueNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMaxValueIncluded(false);
        assertTrue(iv.isValid(0));
        assertTrue(iv.isValid(9));
        assertFalse(iv.isValid(10));
        assertFalse(iv.isValid(11));
        assertFalse(iv.isValid(-1));
    }

    public void testMinMaxValuesNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMinValueIncluded(false);
        iv.setMaxValueIncluded(false);

        assertFalse(iv.isValid(0));
        assertTrue(iv.isValid(1));
        assertTrue(iv.isValid(9));
        assertFalse(iv.isValid(10));
        assertFalse(iv.isValid(11));
        assertFalse(iv.isValid(-1));
    }
}
