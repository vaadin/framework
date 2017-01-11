package com.vaadin.tests.elements.table;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TableScrollTest extends MultiBrowserTest {
    private static final int SCROLL_VALUE = 200;

    @Test
    public void testScrollLeft() {
        openTestURL();
        TableElement table = $(TableElement.class).get(0);
        table.scrollLeft(SCROLL_VALUE);
        Assert.assertEquals(SCROLL_VALUE, getScrollLeftValue(table));
    }

    @Test
    public void testScrollTop() {
        openTestURL();
        TableElement table = $(TableElement.class).get(0);
        table.scroll(SCROLL_VALUE);
        Assert.assertEquals(SCROLL_VALUE, getScrollTopValue(table));
    }

    // helper functions
    private int getScrollTopValue(WebElement elem) {
        JavascriptExecutor js = getCommandExecutor();
        String jsScript = "return arguments[0].getElementsByClassName(\"v-scrollable\")[0].scrollTop;";
        Long scrollTop = (Long) js.executeScript(jsScript, elem);
        return scrollTop.intValue();
    }

    private int getScrollLeftValue(WebElement elem) {
        JavascriptExecutor js = getCommandExecutor();
        String jsScript = "return arguments[0].getElementsByClassName(\"v-scrollable\")[0].scrollLeft;";
        Long scrollLeft = (Long) js.executeScript(jsScript, elem);
        return scrollLeft.intValue();
    }
}
