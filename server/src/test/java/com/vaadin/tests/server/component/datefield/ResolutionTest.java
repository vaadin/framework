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
package com.vaadin.tests.server.component.datefield;

import java.util.ArrayList;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.util.TestUtil;

public class ResolutionTest {

    @Test
    public void testResolutionHigherOrEqualToYear() {
        Iterable<DateResolution> higherOrEqual = DateResolution
                .getResolutionsHigherOrEqualTo(DateResolution.YEAR);
        ArrayList<DateResolution> expected = new ArrayList<>();
        expected.add(DateResolution.YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);
    }

    @Test
    public void testResolutionHigherOrEqualToDay() {
        Iterable<DateResolution> higherOrEqual = DateResolution
                .getResolutionsHigherOrEqualTo(DateResolution.DAY);
        ArrayList<DateResolution> expected = new ArrayList<>();
        expected.add(DateResolution.DAY);
        expected.add(DateResolution.MONTH);
        expected.add(DateResolution.YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);

    }

    @Test
    public void testResolutionLowerThanYear() {
        Iterable<DateResolution> higherOrEqual = DateResolution
                .getResolutionsLowerThan(DateResolution.YEAR);
        ArrayList<DateResolution> expected = new ArrayList<>();
        expected.add(DateResolution.MONTH);
        expected.add(DateResolution.DAY);
        TestUtil.assertIterableEquals(expected, higherOrEqual);

    }
}
