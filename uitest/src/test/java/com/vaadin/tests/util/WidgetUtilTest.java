package com.vaadin.tests.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.widgetset.server.WidgetUtilUI;

public class WidgetUtilTest extends MultiBrowserTest {

    @Test
    public void testBlockElementRequiredSizeComputedStyle() {
        openTestURL();
        WebElement testComponent = findElement(
                By.className("v-widget-util-test"));
        testComponent.click();

        int padding = (int) Math.ceil(2.4 + 3.5);
        int border = (int) Math.ceil(1.8 * 2);
        int baseWidth = 300;
        int baseHeight = 50;

        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())
                && getDesiredCapabilities().getVersion().equals("1")) {
            // PhantomJS1 rounds padding to integers
            padding = 2 + 3;
        }

        if (browserRoundsBorderToInteger(getDesiredCapabilities())) {
            border = 1 * 2;
        }

        assertExpectedSize(testComponent, "noBorderPadding",
                baseWidth + "x" + baseHeight);

        assertExpectedSize(testComponent, "border",
                (baseWidth + border) + "x" + (baseHeight + border));

        assertExpectedSize(testComponent, "padding",
                (baseWidth + padding) + "x" + (baseHeight + padding));

        assertExpectedSize(testComponent, "borderPadding",
                (baseWidth + border + padding) + "x"
                        + (baseHeight + border + padding));

    }

    private void assertExpectedSize(WebElement testComponent, String id,
            String size) {
        WebElement e = testComponent.findElement(By.id(id));
        assertEquals(id + ": " + size, e.getText());
    }

    private boolean browserRoundsBorderToInteger(
            DesiredCapabilities capabilities) {
        // Note that this is how the Windows browsers in the test cluster work.
        // On Mac, Firefox works slightly differently (rounds border to 1.5px).
        return (BrowserUtil.isPhantomJS(capabilities)
                || BrowserUtil.isFirefox(capabilities));
    }

    @Override
    protected Class<?> getUIClass() {
        return WidgetUtilUI.class;
    }
}
