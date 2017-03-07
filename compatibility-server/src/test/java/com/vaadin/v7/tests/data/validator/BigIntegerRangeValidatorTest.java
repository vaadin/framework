/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.v7.tests.data.validator;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.v7.data.validator.BigIntegerRangeValidator;

public class BigIntegerRangeValidatorTest {

    private BigIntegerRangeValidator cleanValidator = new BigIntegerRangeValidator(
            "no values", null, null);
    private BigIntegerRangeValidator minValidator = new BigIntegerRangeValidator(
            "no values", BigInteger.valueOf(10), null);
    private BigIntegerRangeValidator maxValidator = new BigIntegerRangeValidator(
            "no values", null, BigInteger.valueOf(100));
    private BigIntegerRangeValidator minMaxValidator = new BigIntegerRangeValidator(
            "no values", BigInteger.valueOf(10), BigInteger.valueOf(100));

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
                cleanValidator.isValid(BigInteger.valueOf(-15)));
        Assert.assertTrue("Didn't accept valid value",
                minValidator.isValid(BigInteger.valueOf(15)));
        Assert.assertFalse("Accepted too small value",
                minValidator.isValid(BigInteger.valueOf(9)));
    }

    @Test
    public void testMaxValue() {
        Assert.assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(BigInteger.valueOf(1120)));
        Assert.assertTrue("Didn't accept valid value",
                maxValidator.isValid(BigInteger.valueOf(15)));
        Assert.assertFalse("Accepted too large value",
                maxValidator.isValid(BigInteger.valueOf(120)));
    }

    @Test
    public void testMinMaxValue() {
        Assert.assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(BigInteger.valueOf(15)));
        Assert.assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(BigInteger.valueOf(99)));
        Assert.assertFalse("Accepted too small value",
                minMaxValidator.isValid(BigInteger.valueOf(9)));
        Assert.assertFalse("Accepted too large value",
                minMaxValidator.isValid(BigInteger.valueOf(110)));
    }
}
