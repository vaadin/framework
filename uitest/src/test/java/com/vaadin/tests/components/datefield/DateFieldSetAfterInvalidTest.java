package com.vaadin.tests.components.datefield;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldSetAfterInvalidTest extends MultiBrowserTest {

    private static final org.openqa.selenium.By ERROR_INDICATOR_BY = By
            .className("v-errorindicator");

    private static String INVALID_TEXT = "abc";

    @Test
    public void setValueAfterBeingInvalid() {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now().minus(5, DAYS));
        assertNoErrorIndicator();

        dateField.setValue(dateField.getValue() + INVALID_TEXT);
        assertErrorIndicator();

        $(ButtonElement.class).caption("Today").first().click();

        assertFalse(dateField.getValue().endsWith(INVALID_TEXT));
        assertNoErrorIndicator();
    }

    @Test
    public void clearAfterBeingInvalid() {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now().minus(5, DAYS));
        assertNoErrorIndicator();

        dateField.setValue(dateField.getValue() + INVALID_TEXT);
        assertErrorIndicator();

        $(ButtonElement.class).caption("Clear").first().click();

        assertTrue(dateField.getValue().isEmpty());
        assertNoErrorIndicator();

        dateField.setValue(INVALID_TEXT);
        assertErrorIndicator();
    }

    @Test
    public void invalidTypedText() {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setValue(INVALID_TEXT);
        assertErrorIndicator();
    }

    private void assertErrorIndicator() {
        assertElementPresent(ERROR_INDICATOR_BY);
    }

    private void assertNoErrorIndicator() {
        assertElementNotPresent(ERROR_INDICATOR_BY);
    }
}
