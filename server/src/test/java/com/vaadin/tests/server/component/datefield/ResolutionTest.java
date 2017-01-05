package com.vaadin.tests.server.component.datefield;

import java.util.ArrayList;

import org.junit.Test;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.util.TestUtil;

public class ResolutionTest {

    @Test
    public void testResolutionHigherOrEqualToYear() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsHigherOrEqualTo(Resolution.YEAR);
        ArrayList<Resolution> expected = new ArrayList<>();
        expected.add(Resolution.YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);
    }

    @Test
    public void testResolutionHigherOrEqualToDay() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsHigherOrEqualTo(Resolution.DAY);
        ArrayList<Resolution> expected = new ArrayList<>();
        expected.add(Resolution.DAY);
        expected.add(Resolution.MONTH);
        expected.add(Resolution.YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);

    }

    @Test
    public void testResolutionLowerThanYear() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsLowerThan(Resolution.YEAR);
        ArrayList<Resolution> expected = new ArrayList<>();
        expected.add(Resolution.MONTH);
        expected.add(Resolution.DAY);
        TestUtil.assertIterableEquals(expected, higherOrEqual);

    }
}
