package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;

public class ComboBoxItemSizeTest extends SingleBrowserTest {

    ComboBoxElement comboBoxElement;

    @Test
    public void comboBoxItemSizeDisplayCorrectly() {

        openTestURL();

        comboBoxElement = $(ComboBoxElement.class).id("combobox");

        // initial item size include the empty option
        assertItemSizeInPopup(7);

        comboBoxElement.clear();
        sendKeysToInput("black");

        assertItemSizeInPopup(8);

    }

    private void assertItemSizeInPopup(int expectedSize) {
        comboBoxElement.findElement(By.className("v-filterselect-button"))
                .click();
        waitForElementPresent(By.className("v-filterselect-suggestpopup"));
        int itemSize = findElement(By.className("v-filterselect-suggestmenu"))
                .findElements(By.tagName("span")).size();
        assertEquals("There should be " + expectedSize + "items in the popup.",
                expectedSize, itemSize);
    }

    private void sendKeysToInput(CharSequence... keys) {
        // ensure mouse is located over the ComboBox to avoid hover issues
        new Actions(getDriver()).moveToElement(comboBoxElement).perform();
        comboBoxElement.sendKeys(keys);
        comboBoxElement.sendKeys(Keys.ENTER);
        waitForElementNotPresent(By.className("v-filterselect-suggestpopup"));
    }
}
