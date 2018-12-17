package com.vaadin.tests.components.combobox;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertEquals;

public class ComboBoxMixedUpdateTest extends MultiBrowserTest {

    private ComboBoxElement comboBox;
    private ButtonElement reset;
    private ButtonElement show;

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
        waitForElementPresent(By.className("v-filterselect"));
        comboBox = $(ComboBoxElement.class).first();
        reset = $(ButtonElement.class).id("reset");
        show = $(ButtonElement.class).id("show");
    }

    private void sendKeysToInput(CharSequence... keys) {
        comboBox.clear();       
        // ensure mouse is located over the ComboBox to avoid hover issues
        new Actions(getDriver()).moveToElement(comboBox).perform();
        comboBox.sendKeys(keys);
    }

    @Test
    public void testMixedUpdateWorks() {
        comboBox.focus();
        sendKeysToInput("2", Keys.TAB);
        show.click();
        assertEquals("1. Bean value = 2 - ComboBox value = 2", getLogRow(0));
        reset.click();
        show.click();
        assertEquals("2. Bean value = 0 - ComboBox value = 0", getLogRow(0));
        sendKeysToInput("2", Keys.TAB);
        show.click();
        assertEquals("3. Bean value = 2 - ComboBox value = 2", getLogRow(0));
    }
}
