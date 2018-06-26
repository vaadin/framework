package com.vaadin.tests.components.abstractcomponent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ContextClickUITest extends MultiBrowserTest {

    @Test
    public void testContextClick() {
        openTestURL();

        new Actions(getDriver())
                .moveToElement($(UIElement.class).first(), 10, 10)
                .contextClick().perform();

        assertEquals("Context click not received correctly",
                "1. Received context click at (10, 10)", getLogRow(0));
    }

    @Test
    public void testRemoveListener() {
        openTestURL();

        $(ButtonElement.class).first().click();

        new Actions(getDriver())
                .moveToElement($(UIElement.class).first(), 50, 50)
                .contextClick().perform();

        new Actions(getDriver())
                .moveToElement($(UIElement.class).first(), 10, 10).click()
                .perform();

        assertTrue("Context click should not be handled.",
                getLogRow(0).trim().isEmpty());
    }
}
