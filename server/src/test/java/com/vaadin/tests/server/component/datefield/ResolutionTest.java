package com.vaadin.tests.server.component.datefield;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.util.TestUtil;

public class ResolutionTest extends TestCase {

    public void testResolutionHigherOrEqualToYear() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsHigherOrEqualTo(Resolution.YEAR);
        ArrayList<Resolution> expected = new ArrayList<Resolution>();
        expected.add(Resolution.YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);
    }

    public void testResolutionHigherOrEqualToDay() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsHigherOrEqualTo(Resolution.DAY);
        ArrayList<Resolution> expected = new ArrayList<Resolution>();
        expected.add(Resolution.DAY);
        expected.add(Resolution.MONTH);
        expected.add(Resolution.YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);

    }

    public void testResolutionLowerThanDay() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsLowerThan(Resolution.DAY);
        ArrayList<Resolution> expected = new ArrayList<Resolution>();
        expected.add(Resolution.HOUR);
        expected.add(Resolution.MINUTE);
        expected.add(Resolution.SECOND);
        TestUtil.assertIterableEquals(expected, higherOrEqual);

    }

    public void testResolutionLowerThanSecond() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsLowerThan(Resolution.SECOND);
        ArrayList<Resolution> expected = new ArrayList<Resolution>();
        TestUtil.assertIterableEquals(expected, higherOrEqual);
    }

    public void testResolutionLowerThanYear() {
        Iterable<Resolution> higherOrEqual = Resolution
                .getResolutionsLowerThan(Resolution.YEAR);
        ArrayList<Resolution> expected = new ArrayList<Resolution>();
        expected.add(Resolution.MONTH);
        expected.add(Resolution.DAY);
        expected.add(Resolution.HOUR);
        expected.add(Resolution.MINUTE);
        expected.add(Resolution.SECOND);
        TestUtil.assertIterableEquals(expected, higherOrEqual);

    }
}
