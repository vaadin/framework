package com.vaadin.tests.themes.valo;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for disabled label opacity.
 *
 * @author Vaadin Ltd
 */
public class DisabledLabelTest extends MultiBrowserTest {

    @Test
    public void disabledLabelOpacity() {
        openTestURL();

        WebElement enabled = findElement(By.className("my-enabled"));
        String enabledOpacity = enabled.getCssValue("opacity");

        WebElement disabled = findElement(By.className("my-disabled"));
        String disabledOpacity = disabled.getCssValue("opacity");

        Assert.assertNotEquals(
                "Opacity value is the same for enabled and disabled label",
                enabledOpacity, disabledOpacity);
    }
}
