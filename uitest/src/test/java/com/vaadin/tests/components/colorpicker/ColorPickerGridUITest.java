package com.vaadin.tests.components.colorpicker;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class ColorPickerGridUITest extends SingleBrowserTest {

    @Test
    public void testNoError() throws Exception {
        openTestURL();

        // find the color picker grid and click on the second color
        WebElement grid = getDriver()
                .findElement(By.className("v-colorpicker-grid"));
        // click on the second color
        grid.findElements(By.tagName("td")).get(1).click();

        // check that the color picker does not have component error set
        if (hasCssClass(grid, "v-colorpicker-grid-error")) {
            fail("ColorPickerGrid should not have an active component error");
        }
    }
}
