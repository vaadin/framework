package com.vaadin.tests.components.formlayout;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for form layout click listener.
 *
 * @author Vaadin Ltd
 */
public class FormLayoutClickListenerTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        openTestURL();
        waitForElementPresent(By.id("label"));
    }

    @Test
    public void layoutClickListener_clickOnLayout_childAndClickedComponentsAreNull() {
        FormLayoutElement element = $(FormLayoutElement.class).first();
        Actions actions = new Actions(getDriver());
        actions.moveByOffset(element.getLocation().getX() + 2,
                element.getLocation().getY() + 2).click().build().perform();
        waitForLogRowUpdate();

        assertEquals("Source component for click event must be form",
                "3. Source component: form", getLogRow(0));
        assertEquals("Clicked component for click event must be null",
                "2. Clicked component: null", getLogRow(1));
        assertEquals("Child component for click event must be null",
                "1. Child component: null", getLogRow(2));
    }

    @Test
    public void layoutClickListener_clickOnLabel_lableIsChildAndClickedComponent() {
        findElement(By.id("label")).click();
        waitForLogRowUpdate();

        assertEquals("Source component for click event must be form",
                "3. Source component: form", getLogRow(0));
        assertEquals("Clicked component for click event must be label",
                "2. Clicked component: label", getLogRow(1));
        assertEquals("Child component for click event must be label",
                "1. Child component: label", getLogRow(2));
    }

    private void waitForLogRowUpdate() {
        waitUntil(new ExpectedCondition<Boolean>() {

            @Override
            public Boolean apply(WebDriver input) {
                return !getLogRow(2).trim().isEmpty();
            }

            @Override
            public String toString() {
                // Timed out after 10 seconds waiting for ...
                return "log rows to be updated";
            }
        });
    }

}
