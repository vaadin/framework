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
package com.vaadin.data.validator;

import java.time.LocalDate;

import org.junit.Test;

public class RangeValidatorTest extends ValidatorTestBase {

    @Test
    public void testIntegerRangeValidIntPasses() {
        assertPasses(10,
                RangeValidator.of("Must be between -123 and 42", -123, 42));
    }

    @Test
    public void testIntegerRangeInvalidIntFails() {
        assertFails(123,
                RangeValidator.of("Must be between -123 and 42", -123, 42));
    }

    @Test
    public void testRangeWithoutUpperBoundLargeIntegerPasses() {
        assertPasses(Integer.MAX_VALUE,
                RangeValidator.of("Must be at least 18", 18, null));
    }

    @Test
    public void testRangeWithoutUpperBoundSmallIntegerFails() {
        assertFails(17, RangeValidator.of("Must be at least 18", 18, null));
    }

    @Test
    public void testRangeWithoutLowerBoundSmallIntegerPasses() {
        assertPasses(Integer.MIN_VALUE,
                RangeValidator.of("Must be at most 0", null, 0));
    }

    @Test
    public void testRangeWithoutLowerBoundLargeIntegerFails() {
        assertFails(1, RangeValidator.of("Must be at most 0", null, 0));
    }

    @Test
    public void testUnboundedRangePassesEverything() {
        RangeValidator<Integer> v = RangeValidator.of("This should not happen!",
                null, null);

        assertPasses(Integer.MIN_VALUE, v);
        assertPasses(0, v);
        assertPasses(null, v);
        assertPasses(Integer.MAX_VALUE, v);
    }

    @Test
    public void testBoundsInclusiveByDefault() {
        RangeValidator<Integer> v = RangeValidator
                .of("Must be between -10 and 10", -10, 10);

        assertPasses(-10, v);
        assertPasses(10, v);
    }

    @Test
    public void testUpperBoundExclusive() {
        RangeValidator<Integer> v = RangeValidator
                .of("Must be between -10 and 10", -10, 10);
        v.setMaxValueIncluded(false);

        assertPasses(-10, v);
        assertPasses(9, v);
        assertFails(10, v);
    }

    @Test
    public void testLowerBoundExclusive() {
        RangeValidator<Integer> v = RangeValidator
                .of("Must be between -10 and 10", -10, 10);
        v.setMinValueIncluded(false);

        assertFails(-10, v);
        assertPasses(-9, v);
        assertPasses(10, v);
    }

    @Test
    public void testNullLessThanEverything() {
        RangeValidator<Integer> v = RangeValidator.of("Must be any integer",
                Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertPasses(null, v);

        v = RangeValidator.of("Must be very small", null, Integer.MIN_VALUE);
        assertPasses(null, v);
    }

    @Test
    public void testDateRange() {
        RangeValidator<LocalDate> v = RangeValidator.of("Date must be in 2016",
                LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31));

        assertFails(LocalDate.ofEpochDay(0), v);
        assertPasses(LocalDate.of(2016, 7, 31), v);
        assertFails(LocalDate.ofEpochDay(1_000_000_000), v);
    }

}
