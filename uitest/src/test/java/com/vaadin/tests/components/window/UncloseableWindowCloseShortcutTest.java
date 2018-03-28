package com.vaadin.tests.components.window;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class UncloseableWindowCloseShortcutTest extends SingleBrowserTest {

    @Test
    public void testEscShortcut() {
        openTestURL();

        // Hit esc and verify that the Window was not closed.
        driver.findElement(By.cssSelector(".v-window-contents .v-scrollable"))
                .sendKeys(Keys.ESCAPE);
        assertTrue(
                "Uncloseable Window should remain open after esc is pressed.",
                isWindowOpen());
    }

    private boolean isWindowOpen() {
        return $(WindowElement.class).exists();
    }

}
