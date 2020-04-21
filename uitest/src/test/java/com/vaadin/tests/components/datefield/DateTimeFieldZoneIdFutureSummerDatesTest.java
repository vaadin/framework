package com.vaadin.tests.components.datefield;

import static com.vaadin.tests.components.datefield.DateTimeFieldZoneIdFutureSummerDates.END_YEAR_DATEFIELD_ID;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneIdFutureSummerDates.FIXED_RANGE_DATEFIELD_ID;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneIdFutureSummerDates.LOCALE_ID;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneIdFutureSummerDates.START_YEAR_DATEFIELD_ID;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneIdFutureSummerDates.VARIABLE_RANGE_DATEFIELD_ID;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneIdFutureSummerDates.ZONE_ID;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateTimeFieldZoneIdFutureSummerDatesTest
        extends SingleBrowserTest {

    private static final String TESTING_ZONE_ID = "CET";
    private static final String TESTING_LOCALE = Locale.US.toString();

    DateTimeFieldElement dateTimeFieldWithVariableRange;
    DateTimeFieldElement dateTimeFieldWithDefaultRange;

    DateFieldElement transitionsStartYear;
    DateFieldElement transitionsEndYear;

    ComboBoxElement zoneIdComboBox;
    ComboBoxElement localeIdComboBox;

    @Before
    public void init() {
        openTestURL();

        dateTimeFieldWithVariableRange = $(DateTimeFieldElement.class)
                .id(VARIABLE_RANGE_DATEFIELD_ID);
        dateTimeFieldWithDefaultRange = $(DateTimeFieldElement.class)
                .id(FIXED_RANGE_DATEFIELD_ID);

        transitionsStartYear = $(DateFieldElement.class)
                .id(START_YEAR_DATEFIELD_ID);
        transitionsEndYear = $(DateFieldElement.class).id(END_YEAR_DATEFIELD_ID);

        zoneIdComboBox = $(ComboBoxElement.class).id(ZONE_ID);
        zoneIdComboBox.selectByText(TESTING_ZONE_ID);
        localeIdComboBox = $(ComboBoxElement.class).id(LOCALE_ID);
        localeIdComboBox.selectByText(TESTING_LOCALE);
    }

    @Test
    public void dateTimeFieldWithCustomRangeShouldShowDSTWithinRange() {
        final int testingRangeCentralYear = LocalDate.now().getYear() + 50;
        final int testingRangeUpperYear = testingRangeCentralYear + 3;
        final int testingRangeLowerYear = testingRangeCentralYear - 3;

        transitionsEndYear.setDate(LocalDate.of(testingRangeUpperYear, 1, 1));
        transitionsStartYear.setDate(LocalDate.of(testingRangeLowerYear, 1, 1));

        LocalDateTime testingDateTime = LocalDateTime
                .of(testingRangeCentralYear, Month.JULY, 1, 0, 0);
        dateTimeFieldWithVariableRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithVariableRange, "CEST");

        testingDateTime = LocalDateTime.of(testingRangeUpperYear, Month.JULY, 1,
                0, 0);
        dateTimeFieldWithVariableRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithVariableRange, "CEST");

        testingDateTime = LocalDateTime.of(testingRangeLowerYear, Month.JULY, 1,
                0, 0);
        dateTimeFieldWithVariableRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithVariableRange, "CEST");
    }

    @Test
    public void dateTimeFieldWithCustomRangeShouldNotShowDSTOutsideRange() {
        final int testingRangeCentralYear = LocalDate.now().getYear() + 50;
        final int testingRangeUpperYear = testingRangeCentralYear + 3;
        final int testingRangeLowerYear = testingRangeCentralYear - 3;

        transitionsEndYear.setDate(LocalDate.of(testingRangeUpperYear, 1, 1));
        transitionsStartYear.setDate(LocalDate.of(testingRangeLowerYear, 1, 1));

        // This year is out of specified range
        LocalDateTime testingDateTime = LocalDateTime
                .of(LocalDate.now().getYear(), Month.JULY, 1, 0, 0);
        dateTimeFieldWithVariableRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithVariableRange, "CET");

        // One year after the specified range
        testingDateTime = LocalDateTime.of(testingRangeUpperYear + 1,
                Month.JULY, 1, 0, 0);
        dateTimeFieldWithVariableRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithVariableRange, "CET");

        // One year before the specified range
        testingDateTime = LocalDateTime.of(testingRangeLowerYear - 1,
                Month.JULY, 1, 0, 0);
        dateTimeFieldWithVariableRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithVariableRange, "CET");
    }

    @Test
    public void dateTimeFieldWithDefaultRangeShouldShowDSTFrom1980Until20FutureYears() {
        // The 1980 to 20 future years range is the hard-coded default range
        // for which DST is shown if user doesn't provide a custom range

        final int testingRangeLowerYear = 1980;
        LocalDateTime testingDateTime = LocalDateTime.of(testingRangeLowerYear,
                Month.JULY, 1, 0, 0);
        dateTimeFieldWithDefaultRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithDefaultRange, "CEST");

        final int testingRangeUpperYear = LocalDate.now().getYear() + 20;
        testingDateTime = LocalDateTime.of(testingRangeUpperYear, Month.JULY, 1,
                0, 0);
        dateTimeFieldWithDefaultRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithDefaultRange, "CEST");

        final int testingCurrYear = LocalDate.now().getYear();
        testingDateTime = LocalDateTime.of(testingCurrYear, Month.JULY, 1, 0,
                0);
        dateTimeFieldWithDefaultRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithDefaultRange, "CEST");
    }

    @Test
    public void dateTimeFieldWithDefaultRangeShouldNotShowDSTBefore1980() {
        final LocalDateTime testingDateTime = LocalDateTime.of(1979, Month.JULY,
                1, 0, 0);
        dateTimeFieldWithDefaultRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithDefaultRange, "CET");
    }

    @Test
    public void dateTimeFieldWithDefaultRangeShouldNotShowDSTAfter20FutureYears() {
        final LocalDateTime testingDateTime = LocalDateTime
                .of(LocalDate.now().getYear() + 21, Month.JULY, 1, 0, 0);
        dateTimeFieldWithDefaultRange.setDateTime(testingDateTime);
        assertEndsWith(dateTimeFieldWithDefaultRange, "CET");
    }

    private void assertEndsWith(DateTimeFieldElement element, String suffix) {
        final String text = element.getValue();
        assertTrue(text + " should end with " + suffix, text.endsWith(suffix));
    }
}
