package com.vaadin.tests.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class LegacySerializerUITest extends SingleBrowserTest {

    @Test
    public void testInfinity() {
        openTestURL();
        WebElement html = findElement(By.className("gwt-HTML"));
        assertEquals("doubleInfinity: Infinity", html.getText());
        // Can't send infinity back, never have been able to
        assertEquals("1. doubleInfinity: null", getLogRow(0));
    }

}
