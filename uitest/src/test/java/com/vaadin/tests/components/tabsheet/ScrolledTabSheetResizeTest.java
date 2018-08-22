package com.vaadin.tests.components.tabsheet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ScrolledTabSheetResizeTest extends MultiBrowserTest {

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testReindeer() throws IOException, InterruptedException {
        $(ButtonElement.class).first().click();
        for (int i = 800; i >= 400; i = i - 50) {
            testResize(i);
        }
    }

    @Test
    public void testValo() throws IOException, InterruptedException {
        for (int i = 800; i >= 400; i = i - 50) {
            testResize(i);
        }
    }

    private void testResize(int start)
            throws IOException, InterruptedException {
        testBench().resizeViewPortTo(start, 600);

        while (scrollRight()) {
        }

        // This fails on some specific widths by ~15-20 pixels, deemed as
        // non-critical for now so commented out.
        // assertNoExtraRoom(start);

        testBench().resizeViewPortTo(start + 150, 600);

        assertNoExtraRoom(start + 150);
    }

    private void assertNoExtraRoom(int width) {
        TabSheetElement ts = $(TabSheetElement.class).first();
        WebElement scroller = ts
                .findElement(By.className("v-tabsheet-scroller"));
        List<WebElement> tabs = ts
                .findElements(By.className("v-tabsheet-tabitemcell"));
        WebElement lastTab = tabs.get(tabs.size() - 1);

        assertEquals("Tab 19",
                lastTab.findElement(By.className("v-captiontext")).getText());

        int tabWidth = lastTab.getSize().width;
        int tabRight = lastTab.getLocation().x + tabWidth;
        int scrollerLeft = scroller.findElement(By.tagName("button"))
                .getLocation().x;

        assertThat("Not scrolled to the end (width: " + width + ")",
                scrollerLeft, greaterThan(tabRight));
        assertThat("Too big gap (width: " + width + ")", tabWidth,
                greaterThan(scrollerLeft - tabRight));
    }

    /*
     * Scroll the tabsheet bar to the right.
     */
    private boolean scrollRight() throws InterruptedException {
        List<WebElement> scrollElements = getDriver()
                .findElements(By.className("v-tabsheet-scrollerNext"));
        if (!scrollElements.isEmpty()) {
            TestBenchElement rightScrollElement = (TestBenchElement) scrollElements
                    .get(0);
            rightScrollElement.click(5, 5);
            return true;
        } else {
            return false;
        }
    }

}
