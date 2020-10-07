package com.vaadin.tests.components.window;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WindowTwinColSelectTest extends MultiBrowserTest {

    @Test
    public void testBothVisibleInitially() {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();
        TwinColSelectElement twinColSelect = $(TwinColSelectElement.class)
                .first();
        WebElement optionsElement = twinColSelect.getOptionsElement();
        WebElement selectionsElement = twinColSelect.getSelectionsElement();
        assertTrue(optionsElement.isDisplayed());
        assertTrue(selectionsElement.isDisplayed());
        assertEquals("Coordinate is not matching within the tolerance",
                optionsElement.getLocation().getY(),
                selectionsElement.getLocation().getY(), 3);
    }

    @Test
    public void testBothVisibleAfterResize() {
        openTestURL();
        waitForElementPresent(By.className("v-window-resizebox"));
        TwinColSelectElement twinColSelect = $(TwinColSelectElement.class)
                .first();
        new Actions(getDriver())
                .moveToElement(findElement(By.className("v-window-resizebox")))
                .clickAndHold().moveByOffset(-30, -30).release().build()
                .perform();
        WebElement optionsElement = twinColSelect.getOptionsElement();
        WebElement selectionsElement = twinColSelect.getSelectionsElement();
        assertTrue(optionsElement.isDisplayed());
        assertTrue(selectionsElement.isDisplayed());
        assertEquals("Coordinate is not matching within the tolerance.",
                optionsElement.getLocation().getY(),
                selectionsElement.getLocation().getY(), 3);
    }
}
