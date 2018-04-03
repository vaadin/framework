package com.vaadin.tests.extensions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ResponsiveWidthAndHeightTest extends MultiBrowserTest {

    @Before
    public void setUp() throws Exception {
        // We need this in order to ensure that the initial width-range is
        // width: 600px- and height: 500px-
        testBench().resizeViewPortTo(1024, 768);
    }

    @Test
    public void testWidthAndHeightRanges() throws Exception {
        openTestURL();

        // IE sometimes has trouble waiting long enough.
        waitUntil(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".v-csslayout-width-and-height")), 30);

        // Verify both width-range and height-range.
        assertEquals("600px-",
                $(CssLayoutElement.class).first().getAttribute("width-range"));
        assertEquals("500px-",
                $(CssLayoutElement.class).first().getAttribute("height-range"));

        // Resize
        testBench().resizeViewPortTo(550, 450);

        // Verify updated width-range and height-range.
        assertEquals("0-599px",
                $(CssLayoutElement.class).first().getAttribute("width-range"));
        assertEquals("0-499px",
                $(CssLayoutElement.class).first().getAttribute("height-range"));
    }

}
