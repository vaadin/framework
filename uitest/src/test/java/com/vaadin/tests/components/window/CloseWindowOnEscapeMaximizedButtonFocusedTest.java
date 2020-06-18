package com.vaadin.tests.components.window;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import static org.junit.Assert.assertTrue;

public class CloseWindowOnEscapeMaximizedButtonFocusedTest
        extends MultiBrowserTest {

    @Test
    public void windowIsClosed() {
        openTestURL();
        ButtonElement openWindow = $(ButtonElement.class).id("openW");
        openWindow.click();

        WindowElement window = $(WindowElement.class).first();
        window.maximize();
        findElement(By.className("v-window-restorebox")).sendKeys(Keys.ESCAPE);
        waitForElementNotPresent(By.className("v-window"));
        assertTrue("Window should be removed after ESC key is pressed",
                driver.findElements(By.className("v-window ")).isEmpty());
    }
}
