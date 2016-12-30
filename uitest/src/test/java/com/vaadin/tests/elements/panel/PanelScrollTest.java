package com.vaadin.tests.elements.panel;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class PanelScrollTest extends MultiBrowserTest {
    private static final int SCROLL_VALUE = 300;

    @Test
    public void testScrollLeft() throws InterruptedException {
        openTestURL();
        PanelElement panel = $(PanelElement.class).get(0);
        panel.scrollLeft(SCROLL_VALUE);
        Assert.assertEquals(SCROLL_VALUE, getScrollLeftValue(panel));
    }

    @Test
    public void testScrollTop() {
        openTestURL();
        PanelElement panel = $(PanelElement.class).get(0);
        panel.scroll(SCROLL_VALUE);
        Assert.assertEquals(SCROLL_VALUE, getScrollTopValue(panel));
    }

    // helper functions
    private int getScrollTopValue(WebElement elem) {
        Long scrollTop = (Long) executeScript(
                "return arguments[0].getElementsByClassName(\"v-scrollable\")[0].scrollTop;",
                elem);
        return scrollTop.intValue();
    }

    private int getScrollLeftValue(WebElement elem) {
        Long scrollTop = (Long) executeScript(
                "return arguments[0].getElementsByClassName(\"v-scrollable\")[0].scrollLeft;",
                elem);
        return scrollTop.intValue();
    }
}
