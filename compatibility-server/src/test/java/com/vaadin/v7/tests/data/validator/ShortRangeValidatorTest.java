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

import com.vaadin.v7.data.validator.ShortRangeValidator;

public class ShortRangeValidatorTest {

    private ShortRangeValidator cleanValidator = new ShortRangeValidator(
            "no values", null, null);
    private ShortRangeValidator minValidator = new ShortRangeValidator(
            "no values", (short) 10, null);
    private ShortRangeValidator maxValidator = new ShortRangeValidator(
            "no values", null, (short) 100);
    private ShortRangeValidator minMaxValidator = new ShortRangeValidator(
            "no values", (short) 10, (short) 100);

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
                cleanValidator.isValid((short) -15));
        assertTrue("Didn't accept valid value",
                minValidator.isValid((short) 15));
        assertFalse("Accepted too small value",
                minValidator.isValid((short) 9));
    }

    @Test
    public void testMaxValue() {
        assertTrue("Validator without ranges didn't accept value",
                cleanValidator.isValid((short) 1120));
        assertTrue("Didn't accept valid value",
                maxValidator.isValid((short) 15));
        assertFalse("Accepted too large value",
                maxValidator.isValid((short) 120));
    }

    @Test
    public void testMinMaxValue() {
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid((short) 15));
        assertTrue("Didn't accept valid value",
                minMaxValidator.isValid((short) 99));
        assertFalse("Accepted too small value",
                minMaxValidator.isValid((short) 9));
        assertFalse("Accepted too large value",
                minMaxValidator.isValid((short) 110));
    }
}
