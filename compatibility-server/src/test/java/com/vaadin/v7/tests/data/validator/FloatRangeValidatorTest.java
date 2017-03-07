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

import com.vaadin.v7.data.validator.FloatRangeValidator;

public class FloatRangeValidatorTest {

    private FloatRangeValidator cleanValidator = new FloatRangeValidator(
            "no values", null, null);
    private FloatRangeValidator minValidator = new FloatRangeValidator(
            "no values", 10.1f, null);
    private FloatRangeValidator maxValidator = new FloatRangeValidator(
            "no values", null, 100.1f);
    private FloatRangeValidator minMaxValidator = new FloatRangeValidator(
            "no values", 10.5f, 100.5f);

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
                cleanValidator.isValid(-15.0f));
        assertTrue("Didn't accept valid value", minValidator.isValid(10.1f));
        assertFalse("Accepted too small value", minValidator.isValid(10.0f));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid(1120.0f));
        assertTrue("Didn't accept valid value", maxValidator.isValid(15.0f));
        assertFalse("Accepted too large value", maxValidator.isValid(100.6f));
    }

    @Test
    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value", minMaxValidator.isValid(10.5f));
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid(100.5f));
        assertFalse("Accepted too small value", minMaxValidator.isValid(10.4f));
        assertFalse("Accepted too large value",
                minMaxValidator.isValid(100.6f));
    }
}
