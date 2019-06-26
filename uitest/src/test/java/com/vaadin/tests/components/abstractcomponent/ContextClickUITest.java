package com.vaadin.tests.components.abstractcomponent;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContextClickUITest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingContextMenu();
    }

    @Test
    public void testContextClick() {
        openTestURL();

        UIElement uiElement = $(UIElement.class).first();
        new Actions(getDriver())
                .moveToElement(uiElement, getXOffset(uiElement, 10), getYOffset(uiElement, 10))
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
