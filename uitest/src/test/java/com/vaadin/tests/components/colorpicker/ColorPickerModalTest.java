package com.vaadin.tests.components.colorpicker;

import com.vaadin.tests.tb3.MultiBrowserTest;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ColorPickerModalTest extends MultiBrowserTest {

    @Test
    public void testNoError() {
        openTestURL();
        try {
            setDebug(true);
            WebElement cp = getDriver().findElement(By.id("colorP"));
            cp.click();
            assertEquals("Errors present in console", 0,
                    findElements(By.className("SEVERE"))
                            .size());
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}
