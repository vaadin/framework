package com.vaadin.tests.data.validator;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.validator.LegacyBigDecimalRangeValidator;

public class BigDecimalRangeValidatorTest {

    private LegacyBigDecimalRangeValidator cleanValidator = new LegacyBigDecimalRangeValidator(
            "no values", null, null);
    private LegacyBigDecimalRangeValidator minValidator = new LegacyBigDecimalRangeValidator(
            "no values", new BigDecimal(10.1), null);
    private LegacyBigDecimalRangeValidator maxValidator = new LegacyBigDecimalRangeValidator(
            "no values", null, new BigDecimal(100.1));
    private LegacyBigDecimalRangeValidator minMaxValidator = new LegacyBigDecimalRangeValidator(
            "no values", new BigDecimal(10.5), new BigDecimal(100.5));

    @Test
    public void testNullValue() {
        Assert.assertTrue("Didn't accept null", cleanValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", maxValidator.isValid(null));
        Assert.assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        Assert.assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(new BigDecimal(-15.0)));
        Assert.assertTrue("Didn't accept valid value",
                minValidator.isValid(new BigDecimal(10.1)));
        Assert.assertFalse("Accepted too small value",
                minValidator.isValid(new BigDecimal(10.0)));
    }

    @Test
    public void testMaxValue() {
        Assert.assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(new BigDecimal(1120.0)));
        Assert.assertTrue("Didn't accept valid value",
                maxValidator.isValid(new BigDecimal(15.0)));
        Assert.assertFalse("Accepted too large value",
                maxValidator.isValid(new BigDecimal(100.6)));
    }

    @Test
    public void testMinMaxValue() {
        Assert.assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(new BigDecimal(10.5)));
        Assert.assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(new BigDecimal(100.5)));
        Assert.assertFalse("Accepted too small value",
                minMaxValidator.isValid(new BigDecimal(10.4)));
        Assert.assertFalse("Accepted too large value",
                minMaxValidator.isValid(new BigDecimal(100.6)));
    }
}
