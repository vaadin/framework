package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ContextProtocolTest extends SingleBrowserTest {

    @Test
    public void contextPathCorrect() {
        openTestURL();
        // Added by bootstrap
        assertEquals("said", executeScript("return window.hello"));
        // Added by client side
        assertEquals(getBaseURL() + "/statictestfiles/image.png",
                findElement(By.id("image")).getAttribute("src"));
    }

}
