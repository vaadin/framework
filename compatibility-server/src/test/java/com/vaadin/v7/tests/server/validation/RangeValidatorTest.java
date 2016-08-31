package com.vaadin.v7.tests.server.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.validator.IntegerRangeValidator;

public class RangeValidatorTest {

    // This test uses IntegerRangeValidator for simplicity.
    // IntegerRangeValidator contains no code so we really are testing
    // RangeValidator
    @Test
    public void testMinValueNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMinValueIncluded(false);
        assertFalse(iv.isValid(0));
        assertTrue(iv.isValid(10));
        assertFalse(iv.isValid(11));
        assertFalse(iv.isValid(-1));
    }

    @Test
    public void testMinMaxValuesInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        assertTrue(iv.isValid(0));
        assertTrue(iv.isValid(1));
        assertTrue(iv.isValid(10));
        assertFalse(iv.isValid(11));
        assertFalse(iv.isValid(-1));
    }

    @Test
    public void testMaxValueNonInclusive() {
        IntegerRangeValidator iv = new IntegerRangeValidator("Failed", 0, 10);
        iv.setMaxValueIncluded(false);
        assertTrue(iv.isValid(0));
        assertTrue(iv.isValid(9));
        assertFalse(iv.isValid(10));
        assertFalse(iv.isValid(11));
        assertFalse(iv.isValid(-1));
    }

    @Test
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
