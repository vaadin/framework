package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldMonthResolutionStatusChangeTest
        extends MultiBrowserTest {

    @Test
    public void testChangeReadOnly() {
        openTestURL();
        DateFieldElement df = $(DateFieldElement.class).first();
        assertEquals("Unexpected initial date.", "1/19", df.getValue());

        // switch read-only state
        $(ButtonElement.class).id("readOnly").click();

        assertEquals("Unexpected date change.", "1/19", df.getValue());
    }

    @Test
    public void testAddRange() {
        openTestURL();
        DateFieldElement df = $(DateFieldElement.class).first();
        assertEquals("Unexpected initial date.", "1/19", df.getValue());

        // add range
        $(ButtonElement.class).id("addRange").click();

        assertEquals("Unexpected date change.", "1/19", df.getValue());
    }

    @Test
    public void testPopupOpenWithDateNotInRange() {
        openTestURL();
        DateFieldElement df = $(DateFieldElement.class).first();

        // set value before range
        $(ButtonElement.class).id("resetValue").click();
        // add range, previously set date is not in range
        $(ButtonElement.class).id("addRange").click();

        // Test that popup still opens
        df.openPopup();
        waitForElementPresent(By.className("v-datefield-popup"));
        assertElementPresent(By.className("v-datefield-popup"));
    }

}
