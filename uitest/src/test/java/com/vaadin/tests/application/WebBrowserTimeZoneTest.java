package com.vaadin.tests.application;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class WebBrowserTimeZoneTest extends MultiBrowserTest {
    @Test
    public void testBrowserTimeZoneInfo() throws Exception {
        openTestURL();
        $(ButtonElement.class).first().click();

        // Ask TimeZone from browser
        String tzOffset = ((JavascriptExecutor) getDriver())
                .executeScript("return new Date().getTimezoneOffset()")
                .toString();

        // Check that server got the same value.
        assertLabelText("Browser raw offset", tzOffset);
    }

    private void assertLabelText(String caption, String expected) {
        String actual = $(LabelElement.class).caption(caption).first()
                .getText();
        assertEquals(String.format("Unexpected text in label '%s',", caption),
                expected, actual);
    }
}
