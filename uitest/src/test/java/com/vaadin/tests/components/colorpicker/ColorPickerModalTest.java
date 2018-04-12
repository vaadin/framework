package com.vaadin.tests.components.colorpicker;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class ColorPickerModalTest extends MultiBrowserTest {

    @Test
    public void testNoError() {
        openTestURL();
        WebElement cp = getDriver().findElement(By.id("colorP"));
        cp.click();
        WebElement label = findElement(By.id("Log_row_0"));
        Assert.assertEquals(false,
                label.getText().contains("Exception caught"));
    }
}