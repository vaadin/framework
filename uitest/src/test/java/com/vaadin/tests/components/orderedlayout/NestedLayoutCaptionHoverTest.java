package com.vaadin.tests.components.orderedlayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests hovering over caption in nested layout
 */
public class NestedLayoutCaptionHoverTest extends MultiBrowserTest {

    @Test
    public void testTooltipInNestedLayout() throws Exception {
        openTestURL();

        WebElement caption = getDriver()
                .findElement(By.className("v-captiontext"));

        assertEquals("inner layout", caption.getText());

        // Hover over the caption
        new Actions(getDriver()).moveToElement(caption).perform();
        sleep(1000);

        String selector = "Root/VNotification[0]";
        try {
            // Verify that there's no error notification
            vaadinElement(selector);
            fail("No error notification should be found");
        } catch (NoSuchElementException e) {
            // Exception caught. Verify it's the right one.
            assertTrue(e.getMessage().contains(selector));
        }
    }
}