package com.vaadin.tests.components.abstractcomponent;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertTrue;

public class ContextClickUITest extends MultiBrowserTest {

    @Test
    public void testContextClick() {
        openTestURL();

        final UIElement uiElement = $(UIElement.class).first();

        new Actions(getDriver()).moveToElement(uiElement,
                getXOffset(uiElement, 10), getYOffset(uiElement, 10))
                .contextClick().perform();

        assertTrue("Context click not received correctly",
                 getLogRow(0).contains("Received context click"));
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
