package com.vaadin.tests.extensions;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CssLayoutElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SetThemeAndResponsiveLayoutTest extends MultiBrowserTest {

    @Before
    public void setUp() throws Exception {
        // We need this in order to ensure that the initial width-range is
        // width: 600px- and height: 500px-
        testBench().resizeViewPortTo(1024, 768);
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Seems like stylesheet onload is not fired on PhantomJS
        // https://github.com/ariya/phantomjs/issues/12332
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void testWidthAndHeightRanges() throws Exception {
        openTestURL();
        // IE sometimes has trouble waiting long enough.
        new WebDriverWait(getDriver(), 30)
                .until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector(".v-csslayout-width-and-height")));
        // set the theme programmatically
        $(ButtonElement.class).caption("Set theme").first().click();
        new WebDriverWait(getDriver(), 30).until(ExpectedConditions
                .presenceOfElementLocated(By.xpath("//div[@width-range]")));

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
