package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateFieldCursorPositionTest extends MultiBrowserTest {

    @Before
    public void setUp() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void verifyCursorPositionIE() {
        DateFieldElement df = $(DateFieldElement.class).first();
        df.openPopup();
        // 2 row, 2 day
        findElement(By.id("PID_VAADIN_POPUPCAL-2-1")).click();
        sleep(350);
        assertElementNotPresent(By.className("v-datefield-popup"));
        long position = (long) executeScript(
                "return arguments[0].selectionStart;", df.getInputElement());
        assertEquals(String.format(
                "The cursor should be in the end , but was at %d position",
                position), 10, position);
    }

    @Test
    public void verifyCursorPositionChrome() {
        // In Chrome problem occurs only after the first selection has been
        // already performed.
        DateFieldElement df = $(DateFieldElement.class).first();
        df.openPopup();
        // 2 row, 2 day
        findElement(By.id("PID_VAADIN_POPUPCAL-2-1")).click();
        df.openPopup();
        // 2 row, 3 day
        findElement(By.id("PID_VAADIN_POPUPCAL-2-2")).click();
        sleep(350);
        assertElementNotPresent(By.className("v-datefield-popup"));
        long position = (long) executeScript(
                "return arguments[0].selectionStart;", df.getInputElement());
        assertEquals(String.format(
                "The cursor should be in the end , but was at %d position",
                position), 10, position);
    }
}
