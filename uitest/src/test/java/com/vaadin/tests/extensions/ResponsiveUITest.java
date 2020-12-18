package com.vaadin.tests.extensions;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ResponsiveUITest extends MultiBrowserTest {

    @Before
    public void setUp() throws Exception {
        // We need this in order to ensure that the initial width-range is
        // 401px-600px
        testBench().resizeViewPortTo(1024, 768);
    }

    // JQuery style selector
    private WebElement $(String cssSelector) {
        return getDriver().findElement(By.cssSelector(cssSelector));
    }

    @Test
    public void testResizingSplitPanelReflowsLayout() throws Exception {
        openTestURL();

        // IE sometimes has trouble waiting long enough.
        waitUntil(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".v-csslayout-grid.first")), 30);

        assertEquals("401px-600px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        moveSplitter(200);

        assertEquals("601-800",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        moveSplitter(-350);

        assertEquals("201px-400px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("301px-400px",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        compareScreen("responsive");

        moveSplitter(-200);
        assertEquals("-200px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));

        moveSplitter(-100);
        assertEquals("0-100px",
                $(".v-csslayout-grid.second").getAttribute("width-range"));
    }

    private void moveSplitter(int xOffset) {
        new Actions(getDriver()).clickAndHold($(".v-splitpanel-hsplitter"))
                .moveByOffset(xOffset, 0).release().build().perform();
    }

    @Test
    public void testResizingWindowReflowsLayout() throws Exception {
        openTestURL();
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("401px-600px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        testBench().resizeViewPortTo(1224, 768);
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("601-800",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("501px-",
                $(".v-csslayout-grid.second").getAttribute("width-range"));

        testBench().resizeViewPortTo(674, 768);
        waitUntilLoadingIndicatorNotVisible();

        assertEquals("201px-400px",
                $(".v-csslayout-grid.first").getAttribute("width-range"));
        assertEquals("301px-400px",
                $(".v-csslayout-grid.second").getAttribute("width-range"));
    }
}
