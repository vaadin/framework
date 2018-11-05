package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ComboBoxCombinedWithEnterShortcutTest extends MultiBrowserTest {

    @Test
    public void testKeyboardSelection() throws InterruptedException {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.click();
        cb.sendKeys(500, Keys.DOWN, Keys.DOWN, Keys.DOWN, Keys.ENTER);
        assertEquals("", getLogRow(0).trim());
        cb.sendKeys(Keys.ENTER);
        assertEquals("1. Button clicked. ComboBox value: Berlin", getLogRow(0));
    }
}
