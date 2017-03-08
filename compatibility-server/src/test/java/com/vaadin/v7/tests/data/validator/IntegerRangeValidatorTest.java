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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.v7.data.validator.IntegerRangeValidator;

public class IntegerRangeValidatorTest {

    private IntegerRangeValidator cleanValidator = new IntegerRangeValidator(
            "no values", null, null);
    private IntegerRangeValidator minValidator = new IntegerRangeValidator(
            "no values", 10, null);
    private IntegerRangeValidator maxValidator = new IntegerRangeValidator(
            "no values", null, 100);
    private IntegerRangeValidator minMaxValidator = new IntegerRangeValidator(
            "no values", 10, 100);

    @Test
    public void testNullValue() {
        assertTrue("Didn't accept null", cleanValidator.isValid(null));
        assertTrue("Didn't accept null", minValidator.isValid(null));
        assertTrue("Didn't accept null", maxValidator.isValid(null));
        assertTrue("Didn't accept null", minMaxValidator.isValid(null));
    }

    @Test
    public void testMinValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(-15));
        assertTrue("Didn't accept valid value", minValidator.isValid(15));
        assertFalse("Accepted too small value", minValidator.isValid(9));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(1120));
        assertTrue("Didn't accept valid value", maxValidator.isValid(15));
        assertFalse("Accepted too large value", maxValidator.isValid(120));
    }

    @Test
    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(15));
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(99));
        assertFalse("Accepted too small value", minMaxValidator.isValid(9));
        assertFalse("Accepted too large value", minMaxValidator.isValid(110));
    }
}
