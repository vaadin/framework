package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class CustomDateFormatTest extends MultiBrowserTest {

    @Test
    public void checkCustomDateFormat() {
        openTestURL();

        String text = findElement(By.tagName("input")).getAttribute("value");
        assertEquals("1. tammikuuta 2010 klo", text);
    }

}
