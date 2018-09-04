package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class DateFieldResetValueWithinListenerTest extends MultiBrowserTest {

    @Test
    public void testValueReassigned() {
        openTestURL();
        // Click button to change the value to be 5 days ahead. It should cause
        // dateField to reset the value to today
        findElement(By.id("setValueButton")).click();
        String text = findElement(By.tagName("input")).getAttribute("value");
        assertEquals(
                LocalDate.now().format(DateTimeFormatter.ofPattern("d.M.yyyy")),
                text);
    }
}
