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

        DateTimeFormatter format = DateTimeFormatter.ofPattern("d.M.yyyy");
        String textBefore = findElement(By.tagName("input"))
                .getAttribute("value");
        assertEquals(
                DateFieldResetValueWithinListener.initialValue.format(format),
                textBefore);

        findElement(By.id("setValueButton")).click();

        String textAfter = findElement(By.tagName("input"))
                .getAttribute("value");
        assertEquals(DateFieldResetValueWithinListener.beforeInitialValue
                .format(format), textAfter);
    }
}
