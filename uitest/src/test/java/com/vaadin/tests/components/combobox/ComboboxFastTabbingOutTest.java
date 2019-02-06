package com.vaadin.tests.components.combobox;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ComboboxFastTabbingOutTest extends MultiBrowserTest {
    @Test
    public void checkNoPopUpIsOpen() {
        openTestURL();

        ComboBoxElement comboBoxElementFirst = $(ComboBoxElement.class)
                .id("firstCombobox");
        ComboBoxElement comboBoxElementSecond = $(ComboBoxElement.class)
                .id("secondCombobox");

        comboBoxElementFirst.sendKeys("A", Keys.TAB);

        assertEquals(getDriver()
                .findElements(By.className("v-filterselect-suggestpopup"))
                .size(), 0);
        assertFalse(comboBoxElementFirst.isPopupOpen());
        assertFalse(comboBoxElementSecond.isPopupOpen());
    }
}
