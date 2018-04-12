package com.vaadin.tests.components.colorpicker;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.ColorPickerElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class ColorPickerValueTestTest extends MultiBrowserTest {

    @Test
    public void testValue() throws Exception {
        openTestURL();

        // Open ColorPicker
        findElement(By.id("clp")).click();
        // Click Button to change color
        ButtonElement button = $(ButtonElement.class).first();
        button.click();
        Thread.sleep(300);
        assertEquals("#ffff00", getColorpickerValue());
    }

    private String getColorpickerValue() {
        WebElement field = findElement(
                By.className("v-colorpicker-preview-textfield"));
        return field.getAttribute("value");
    }
}
