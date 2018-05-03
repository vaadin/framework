package com.vaadin.tests.components.optiongroup;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for Option group (with new items allowed): unset read only state.
 *
 * @author Vaadin Ltd
 */
public class ReadOnlyOptionGroupTest extends MultiBrowserTest {

    @Test
    public void testOptionGroup() {
        setDebug(true);
        openTestURL();

        WebElement checkbox = driver.findElement(By.className("v-checkbox"));
        WebElement checkboxInput = checkbox.findElement(By.tagName("input"));
        checkboxInput.click();

        assertNoErrorNotifications();

        Assert.assertFalse(
                "Radio button in option group is still disabled "
                        + "after unset reaonly",
                isElementPresent(By.className("v-radiobutton-disabled")));
    }

}
