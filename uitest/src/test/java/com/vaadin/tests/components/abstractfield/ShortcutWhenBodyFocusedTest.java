package com.vaadin.tests.components.abstractfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ShortcutWhenBodyFocusedTest extends SingleBrowserTest {

    @Test
    public void triggerShortcutOnBody() {
        openTestURL();
        ButtonElement b = $(ButtonElement.class).caption("Hello").first();
        b.click();
        assertEquals("1. Hello clicked", getLogRow(0));

        b.sendKeys("A");
        assertEquals("2. Hello clicked", getLogRow(0));

        WebElement body = findElement(By.xpath("//body"));
        body.sendKeys("A");
        assertEquals("3. Hello clicked", getLogRow(0));
    }
}
