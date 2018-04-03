package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 *
 * @author Vaadin Ltd
 */
public class DisabledParentLayoutTest extends MultiBrowserTest {

    @Test
    public void testEnableParentLayout() {
        openTestURL();

        WebElement button = driver.findElement(By.className("v-button"));
        button.click();

        WebElement textField = driver
                .findElement(By.className("v-datefield-textfield"));
        textField.click();

        assertFalse(
                "Date input text field shoud be disabled for disabled DateField",
                textField.isEnabled());

        WebElement dataFieldButton = driver
                .findElement(By.className("v-datefield-button"));
        dataFieldButton.click();

        assertFalse("Disabled date popup is opened after click to its button",
                isElementPresent(By.className("v-datefield-popup")));

        button.click();

        assertTrue(
                "Date input text field should be enabled for enabled DateField",
                textField.isEnabled());

        textField.click();
        String text = "text";
        textField.sendKeys(text);

        assertEquals("Unexpected text in date text field", text,
                textField.getAttribute("value"));

        dataFieldButton.click();
        // Requires two clicks because of error message.
        // TODO fix
        dataFieldButton.click();
        assertFalse("Unexpected disabled element found",
                isElementPresent(By.className("v-disabled")));

        assertTrue("Date popup is not opened after click to its button",
                isElementPresent(By.className("v-datefield-popup")));
    }

}
