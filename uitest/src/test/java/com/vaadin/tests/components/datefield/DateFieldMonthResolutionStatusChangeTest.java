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

}
