package com.vaadin.tests.components.colorpicker;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;

public class ColorPickerUserColorChangeTest extends MultiBrowserTest {

    @Test
    public void testUserOriginatedTrue() {
        openTestURL();
        // Open colorPicker
        findElement(By.className("v-button-v-colorpicker ")).click();
        sleep(100);
        // click somewhere inside the gradient layer
        findElement(By.className("v-colorpicker-gradient-clicklayer")).click();
        // confirm selection by clicking "OK" button
        findElements(By.className("v-button")).stream()
                .filter(el -> el.getText().equals("OK")).findFirst().get()
                .click();

        WebElement label = findElement(By.id("labelValue"));
        assertEquals(true, label.getText().endsWith("true"));

        findElement(By.id("changeColor")).click();
        assertEquals(false, label.getText().endsWith("true"));
        assertEquals(true, label.getText().endsWith("false"));
    }
}
