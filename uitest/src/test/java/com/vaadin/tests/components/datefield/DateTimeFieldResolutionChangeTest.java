package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldResolutionChangeTest extends MultiBrowserTest {

    @Test
    public void testValueAndResolutionChange() {
        openTestURL();

        // set a fixed date
        $(ButtonElement.class).caption("set").first().click();

        // both fields should trigger a value change event but the value should
        // be truncated according to each field's resolution
        assertEquals("Unexpected log row",
                "1. MonthField set value 2021-02-14 16:17:00", getLogRow(3));
        assertEquals("Unexpected log row",
                "2. MonthField value change event: 2021-02-01 00:00:00",
                getLogRow(2));
        assertEquals("Unexpected log row",
                "3. DayField set value 2021-02-14 16:17:00", getLogRow(1));
        assertEquals("Unexpected log row",
                "4. DayField value change event: 2021-02-14 00:00:00",
                getLogRow(0));

        // change both to day resolution
        $(ButtonElement.class).caption("day").first().click();

        // DayField shouldn't react, MonthField should check that the value
        // matches resolution but not trigger a ValueChangeEvent
        assertEquals("Unexpected log row",
                "5. MonthField set value 2021-02-01 00:00:00", getLogRow(0));

        // change both to month resolution
        $(ButtonElement.class).caption("month").first().click();

        // both fields should check that the value matches resolution but only
        // DayField should trigger a ValueChangeEvent
        assertEquals("Unexpected log row",
                "6. MonthField set value 2021-02-01 00:00:00", getLogRow(2));
        assertEquals("Unexpected log row",
                "7. DayField set value 2021-02-01 00:00:00", getLogRow(1));
        assertEquals("Unexpected log row",
                "8. DayField value change event: 2021-02-01 00:00:00",
                getLogRow(0));
    }
}
