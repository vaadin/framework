package com.vaadin.tests.components.combobox;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.newelements.ComboBoxElement;

public class ComboBoxClosePopupRetainTextTest extends MultiBrowserTest {
    @Override
    protected Class<?> getUIClass() {
        return ComboBoxes2.class;
    }

    @Test
    public void testClosePopupRetainText() throws Exception {
        openTestURL();

        ComboBoxElement cb = $(ComboBoxElement.class).first();
        WebElement textbox = cb.findElement(By.vaadin("#textbox"));
        textbox.sendKeys("I");
        cb.openPopup();
        cb.openPopup(); // openPopup() actually toggles
        // The entered value should remain
        assertEquals("I", textbox.getAttribute("value"));
    }

    @Test
    public void testClosePopupRetainText_selectingAValue() throws Exception {
        openTestURL();
        ComboBoxElement cb = $(ComboBoxElement.class).first();
        cb.selectByText("Item 3");
        WebElement textbox = cb.findElement(By.vaadin("#textbox"));
        textbox.clear();
        textbox.sendKeys("I");
        cb.openPopup();
        // Entered value should remain in the text field even though the popup
        // is opened
        assertEquals("I", textbox.getAttribute("value"));

    }

}
