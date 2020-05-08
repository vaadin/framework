package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridWithFullWidthComponentsTest extends MultiBrowserTest {

    @Test
    public void testResizeUpAndDown() {
        openTestURL();

        WebElement hScrollBar = findElement(
                By.className("v-grid-scroller-horizontal"));
        assertEquals("Unexpected horizontal scrollbar visibility", "none",
                hScrollBar.getCssValue("display"));

        // increase the browser size
        getDriver().manage().window().setSize(new Dimension(2000, 850));
        sleep(300);

        assertEquals("Unexpected horizontal scrollbar visibility", "none",
                hScrollBar.getCssValue("display"));

        // scale back again
        getDriver().manage().window().setSize(new Dimension(1500, 850));
        sleep(300);

        assertEquals("Unexpected horizontal scrollbar visibility", "none",
                hScrollBar.getCssValue("display"));
    }

    @Test
    public void testResizeDownAndUp() {
        openTestURL();

        WebElement hScrollBar = findElement(
                By.className("v-grid-scroller-horizontal"));
        assertEquals("Unexpected horizontal scrollbar visibility", "none",
                hScrollBar.getCssValue("display"));

        // decrease the browser size far enough that scrollbars are needed
        getDriver().manage().window().setSize(new Dimension(800, 850));
        sleep(300);

        assertEquals("Unexpected horizontal scrollbar visibility", "block",
                hScrollBar.getCssValue("display"));

        // scale back again
        getDriver().manage().window().setSize(new Dimension(1500, 850));
        sleep(300);

        assertEquals("Unexpected horizontal scrollbar visibility", "none",
                hScrollBar.getCssValue("display"));
    }
}
