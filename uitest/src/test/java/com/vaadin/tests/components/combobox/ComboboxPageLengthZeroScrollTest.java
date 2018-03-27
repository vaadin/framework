package com.vaadin.tests.components.combobox;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.Browser;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test class for testing issue #13488 - changing pages with pagelength=0 breaks
 * the style.
 *
 * @author Vaadin Ltd
 */

public class ComboboxPageLengthZeroScrollTest extends MultiBrowserTest {
    @Test
    public void testComboboxPageLength() {
        openTestURL();

        WebElement comboBox = vaadinElement(
                "/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[0]/VFilterSelect[0]#textbox");

        // navigate to the next page. keyboard navigation is the preferred
        // method here since it's much easier to implement.

        Actions keyNavigation = new Actions(driver).moveToElement(comboBox)
                .click();

        for (int i = 0; i < 25; ++i) {
            keyNavigation.sendKeys(Keys.ARROW_DOWN);

        }
        keyNavigation.perform();

        // The broken behavior always caused a v-shadow element to have
        // height: 10px. Verify that this does no longer happen.

        String cssValue = driver.findElement(By.className("v-shadow"))
                .getCssValue("height");

        Assert.assertNotEquals("v-shadow height should not be 10px", "10px",
                cssValue);

    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowserCapabilities(Browser.IE8);
    }
}
