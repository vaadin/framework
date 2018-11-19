package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import com.vaadin.ui.Button;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComboBoxNewItemAdd2ndTimeTest extends SingleBrowserTest {

    private ComboBoxElement comboBox;

    @Test
    public void addItem_reloadDataProvider_addSameItem() {
        openTestURL();
        String sample = new String("hello");

        comboBox = $(ComboBoxElement.class).first();

        ButtonElement reloadButton = $(ButtonElement.class)
                .caption("Reload Data Provider").first();
        ButtonElement value = $(ButtonElement.class).caption("Get Value")
                .first();

        assertEquals("", comboBox.getValue());

        sendKeysToInput(sample);
        value.click();
        assertEquals(sample, value.getCaption());

        comboBox.clear();
        value.click();
        assertEquals("", value.getCaption());

        reloadButton.click();
        sendKeysToInput(sample);
        value.click();
        assertEquals(sample, value.getCaption());
    }

    private void sendKeysToInput(CharSequence... keys) {
        // ensure mouse is located over the ComboBox to avoid hover issues
        new Actions(getDriver()).moveToElement(comboBox).perform();
        comboBox.sendKeys(keys);
    }
}
