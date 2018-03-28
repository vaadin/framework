package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class CheckBoxGroupFocusBlurTest extends MultiBrowserTest {

    @Test
    public void focusBlurEvents() {
        openTestURL();

        List<WebElement> checkBoxes = $(CheckBoxGroupElement.class).first()
                .findElements(By.tagName("input"));
        $(CheckBoxGroupElement.class).first().selectByText("1");

        // Focus event is fired
        assertTrue(logContainsText("1. Focus Event"));

        $(CheckBoxGroupElement.class).first().selectByText("2");
        // click on the second checkbox doesn't fire anything
        assertFalse(logContainsText("2."));

        // move the cursor to the middle of the first element,
        // offset to the middle of the two and perform click
        new Actions(getDriver()).moveToElement(checkBoxes.get(0))
                .moveByOffset(0,
                        (checkBoxes.get(1).getLocation().y
                                - checkBoxes.get(0).getLocation().y) / 2)
                .click().build().perform();

        // no new events
        assertFalse(logContainsText("2."));

        // click to label of a checkbox
        $(CheckBoxGroupElement.class).first().findElements(By.tagName("label"))
                .get(2).click();
        // no new events
        assertFalse(logContainsText("2."));

        // click on log label => blur
        $(LabelElement.class).first().click();
        // blur event is fired
        assertTrue(logContainsText("2. Blur Event"));

        $(CheckBoxGroupElement.class).first().selectByText("4");
        // Focus event is fired
        assertTrue(logContainsText("3. Focus Event"));

        // move keyboard focus to the next checkbox
        checkBoxes.get(3).sendKeys(Keys.TAB);
        // no new events
        assertFalse(logContainsText("4."));

        // select the next checkbox
        checkBoxes.get(4).sendKeys(Keys.SPACE);
        // no new events
        assertFalse(logContainsText("4."));
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Focus does not move when expected with Selenium/TB and Firefox 45
        return getBrowsersExcludingFirefox();
    }
}
