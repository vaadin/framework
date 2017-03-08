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
