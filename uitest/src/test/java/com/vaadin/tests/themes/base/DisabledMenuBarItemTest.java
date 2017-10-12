package com.vaadin.tests.themes.base;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DisabledMenuBarItemTest extends MultiBrowserTest {

    @Test
    public void disabledMenuItemShouldHaveOpacity() throws IOException {
        openTestURL();

        WebElement element = driver
                .findElement(By.className("v-menubar-menuitem-disabled"));

        assertEquals("0.5", element.getCssValue("opacity"));

        compareScreen("transparent");
    }
}
