package com.vaadin.tests.components.tabsheet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.TabSheetElement;
import com.vaadin.testbench.elements.UIElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ScrolledTabSheetResizeTest extends MultiBrowserTest {

    protected String lastVisibleTabCaption = "Tab 19";

    private WebElement pendingTab = null;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testReindeer() throws IOException, InterruptedException {
        $(ButtonElement.class).first().click();
        Map<String, Integer> sizes = saveWidths();
        StringBuilder exceptions = new StringBuilder();
        boolean failed = false;
        // upper limit is determined by the amount of tabs,
        // lower end by limits set by Selenium version
        for (int i = 1400; i >= 650; i = i - 50) {
            try {
                testResize(i, sizes);
            } catch (Exception e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(i + ": " + e.getMessage());
                if (e.getCause() != null && e.getCause().getMessage() != null
                        && !e.getCause().getMessage().trim().isEmpty()) {
                    exceptions.append(" - " + e.getCause().getMessage());
                }
            } catch (AssertionError e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(i + ": " + e.getMessage());
            }
        }
        if (failed) {
            fail("Combined error report: " + exceptions.toString());
        }
    }

    @Test
    public void testValo() throws IOException, InterruptedException {
        Map<String, Integer> sizes = saveWidths();
        StringBuilder exceptions = new StringBuilder();
        boolean failed = false;
        // 1550 would be better for the amount of tabs (wider than for
        // reindeer), but IE11 can't adjust that far
        for (int i = 1500; i >= 650; i = i - 50) {
            try {
                testResize(i, sizes);
            } catch (Exception e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(i + ": " + e.getMessage());
                if (e.getCause() != null && e.getCause().getMessage() != null
                        && !e.getCause().getMessage().trim().isEmpty()) {
                    exceptions.append(" - " + e.getCause().getMessage());
                }
            } catch (AssertionError e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(i + ": " + e.getMessage());
            }
        }
        if (failed) {
            fail("Combined error report: " + exceptions.toString());
        }
    }

    private Map<String, Integer> saveWidths() {
        // save the tab widths before any scrolling
        TabSheetElement ts = $(TabSheetElement.class).first();
        Map<String, Integer> sizes = new HashMap<>();
        for (WebElement tab : ts
                .findElements(By.className("v-tabsheet-tabitemcell"))) {
            if (hasCssClass(tab, "v-tabsheet-tabitemcell-first")) {
                // skip the first visible for now, it has different styling and
                // we are interested in the non-styled width
                pendingTab = tab;
                continue;
            }
            if (pendingTab != null && tab.isDisplayed()) {
                String currentLeft = tab.getCssValue("padding-left");
                String pendingLeft = pendingTab.getCssValue("padding-left");
                if (currentLeft == null || "0px".equals(currentLeft)) {
                    currentLeft = tab.findElement(By.className("v-caption"))
                            .getCssValue("margin-left");
                    pendingLeft = pendingTab
                            .findElement(By.className("v-caption"))
                            .getCssValue("margin-left");
                }
                if (currentLeft != pendingLeft && currentLeft.endsWith("px")
                        && pendingLeft.endsWith("px")) {
                    WebElement caption = pendingTab
                            .findElement(By.className("v-captiontext"));
                    sizes.put(caption.getAttribute("innerText"),
                            pendingTab.getSize().getWidth()
                                    - intValue(pendingLeft)
                                    + intValue(currentLeft));
                }
                pendingTab = null;
            }
            WebElement caption = tab.findElement(By.className("v-captiontext"));
            sizes.put(caption.getAttribute("innerText"),
                    tab.getSize().getWidth());
        }
        return sizes;
    }

    private Integer intValue(String pixelString) {
        return Integer
                .valueOf(pixelString.substring(0, pixelString.indexOf("px")));
    }

    private void testResize(int start, Map<String, Integer> sizes)
            throws IOException, InterruptedException {
        resizeViewPortTo(start);
        waitUntilLoadingIndicatorNotVisible();
        sleep(100); // a bit more for layouting

        int iterations = 0;
        while (scrollRight() && iterations < 50) {
            waitUntilLoadingIndicatorNotVisible();
            ++iterations;
        }

        if (iterations >= 50) {
            fail("scrolling right never reaches the end");
        }
        assertNoExtraRoom(start, sizes);

        resizeViewPortTo(start + 150);
        waitUntilLoadingIndicatorNotVisible();
        sleep(100); // a bit more for layouting

        assertNoExtraRoom(start + 150, sizes);
    }

    private void resizeViewPortTo(int width) {
        try {
            testBench().resizeViewPortTo(width, 600);
        } catch (UnsupportedOperationException e) {
            // sometimes this exception is thrown even if resize succeeded, test
            // validity
            waitUntilLoadingIndicatorNotVisible();
            UIElement ui = $(UIElement.class).first();
            int currentWidth = ui.getSize().width;
            if (currentWidth != width) {
                // only throw the exception if the size didn't change
                throw e;
            }
        }
    }

    private void assertNoExtraRoom(int width, Map<String, Integer> sizes) {
        TabSheetElement ts = $(TabSheetElement.class).first();
        WebElement scroller = ts
                .findElement(By.className("v-tabsheet-scroller"));
        List<WebElement> tabs = ts
                .findElements(By.className("v-tabsheet-tabitemcell"));
        WebElement lastTab = tabs.get(tabs.size() - 1);

        assertEquals("Unexpected last visible tab,", lastVisibleTabCaption,
                lastTab.findElement(By.className("v-captiontext")).getText());

        WebElement firstHidden = getFirstHiddenViewable(tabs);
        if (firstHidden == null) {
            // nothing to scroll to
            return;
        }
        // the sizes change during a tab's life-cycle, use the recorded size
        // approximation for how much extra space adding this tab would need
        // (measuring a hidden tab would definitely give too small width)
        WebElement caption = firstHidden
                .findElement(By.className("v-captiontext"));
        String captionText = caption.getAttribute("innerText");
        Integer firstHiddenWidth = sizes.get(captionText);
        if (firstHiddenWidth == null) {
            firstHiddenWidth = sizes.get("Tab 3");
        }

        int tabWidth = lastTab.getSize().width;
        int tabRight = lastTab.getLocation().x + tabWidth;
        assertThat("Unexpected tab width", tabRight, greaterThan(20));

        int scrollerLeft = scroller.getLocation().x;
        // technically these should probably be just greaterThan,
        // but one pixel's difference is irrelevant for now
        assertThat("Not scrolled to the end (width: " + width + ")",
                scrollerLeft, greaterThanOrEqualTo(tabRight));
        assertThat("Too big gap (width: " + width + ")", firstHiddenWidth,
                greaterThanOrEqualTo(scrollerLeft - tabRight));
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

    /*
     * There is no way to differentiate between hidden-on-server and
     * hidden-on-client here, so this method has to be overridable.
     */
    protected WebElement getFirstHiddenViewable(List<WebElement> tabs) {
        WebElement previous = null;
        for (WebElement tab : tabs) {
            if (hasCssClass(tab, "v-tabsheet-tabitemcell-first")) {
                break;
            }
            previous = tab;
        }
        return previous;
    }
}
