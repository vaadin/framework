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
