package com.vaadin.tests.components.select;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class SelectWithIntegersTest extends SingleBrowserTest {
    @Test
    public void testSelectWithIntegers() {
        openTestURL();

        ListSelectElement defaultSelect = $(ListSelectElement.class)
                .caption("Default").first();
        ListSelectElement toStringSelect = $(ListSelectElement.class)
                .caption("ID_TOSTRING").first();

        assertEquals("2,014", defaultSelect.getOptions().get(0));
        assertEquals("2014", toStringSelect.getOptions().get(0));
    }
}
