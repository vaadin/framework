package com.vaadin.tests.components.datefield;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldSetAfterInvalidTest extends MultiBrowserTest {

    @Test
    public void setValueAfterBeingInvalid() {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now().minus(5, DAYS));

        String invalidSuffix = "abc";
        dateField.setValue(dateField.getValue() + invalidSuffix);

        $(ButtonElement.class).first().click();

        assertFalse(dateField.getValue().endsWith(invalidSuffix));
    }

    @Test
    public void clearAfterBeingInvalid() {
        openTestURL();

        DateFieldElement dateField = $(DateFieldElement.class).first();
        dateField.setDate(LocalDate.now().minus(5, DAYS));

        String invalidSuffix = "abc";
        dateField.setValue(dateField.getValue() + invalidSuffix);

        $(ButtonElement.class).get(1).click();

        assertTrue(dateField.getValue().isEmpty());
    }
}
