package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@SuppressWarnings("boxing")
@TestCategory("grid")
public abstract class AbstractGridColumnAutoWidthTest extends MultiBrowserTest {

    public static final int TOTAL_MARGIN_PX = 26;
    private static final int tolerance = 10;

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testNarrowHeaderWideBody() {
        WebElement[] col = getColumn(1);
        int headerWidth = col[0].getSize().getWidth();
        int bodyWidth = col[1].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertLessThan("header should've been narrower than body", headerWidth,
                bodyWidth);
        assertEquals("column should've been roughly as wide as the body",
                bodyWidth, colWidth, tolerance);
    }

    @Test
    public void testWideHeaderNarrowBody() {
        WebElement[] col = getColumn(2);
        int headerWidth = col[0].getSize().getWidth();
        int bodyWidth = col[1].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertGreater("header should've been wider than body", headerWidth,
                bodyWidth);
        assertEquals("column should've been roughly as wide as the header",
                headerWidth, colWidth, tolerance);
    }

    @Test
    public void testTooNarrowColumn() {
        if (BrowserUtil.isIE(getDesiredCapabilities())) {
            // IE can't deal with overflow nicely.
            return;
        }

        WebElement[] col = getColumn(3);
        int headerWidth = col[0].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertLessThan("column should've been narrower than content", colWidth,
                headerWidth);
    }

    @Test
    public void testTooWideColumn() {
        WebElement[] col = getColumn(4);
        int headerWidth = col[0].getSize().getWidth();
        int colWidth = col[2].getSize().getWidth() - TOTAL_MARGIN_PX;

        assertGreater("column should've been wider than content", colWidth,
                headerWidth);
    }

    @Test
    public void testColumnsRenderCorrectly() throws IOException {
        WebElement loadingIndicator = findElement(
                By.className("v-loading-indicator"));
        Pattern pattern = Pattern.compile("display: *none;");
        waitUntil(driver -> pattern
                .matcher(loadingIndicator.getAttribute("style")).find());
        compareScreen("grid-v8-initialRender");
    }

    private WebElement[] getColumn(int i) {
        WebElement[] col = new WebElement[3];
        col[0] = getDriver().findElement(
                By.xpath("//thead//th[" + (i + 1) + "]/div[1]/span"));
        col[1] = getDriver()
                .findElement(By.xpath("//tbody//td[" + (i + 1) + "]//span"));
        col[2] = getDriver()
                .findElement(By.xpath("//tbody//td[" + (i + 1) + "]"));
        return col;
    }

}
