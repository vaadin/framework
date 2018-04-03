package com.vaadin.tests.tb3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.By;

/**
 * Base class for TestBench 3+ tests that use tooltips. This class contains
 * utility methods for testing tooltip use.
 *
 * @author Vaadin Ltd
 */
public abstract class TooltipTest extends MultiBrowserTest {

    protected void clearTooltip() throws Exception {
        moveToRoot();
        Thread.sleep(500);
        checkTooltipNotPresent();
    }

    protected void checkTooltip(String locator, String value) throws Exception {
        checkTooltip(By.vaadin(locator), value);
    }

    protected void checkTooltip(org.openqa.selenium.By by, String value)
            throws Exception {
        checkTooltip(getDriver().findElement(by), value);
    }

    protected void checkTooltip(WebElement element, String value)
            throws Exception {
        testBenchElement(element).showTooltip();
        if (null != value) {
            checkTooltip(value);
        } else {
            checkTooltipNotPresent();
        }
    }

    protected void checkTooltip(String value) throws Exception {
        WebElement body = findElement(By.cssSelector("body"));
        WebElement tooltip = getTooltip();
        assertEquals(value, tooltip.getText());
        assertTrue("Tooltip overflowed to the left",
                tooltip.getLocation().getX() >= 0);
        assertTrue("Tooltip overflowed up", tooltip.getLocation().getY() >= 0);
        assertTrue("Tooltip overflowed to the right",
                tooltip.getLocation().getX()
                        + tooltip.getSize().getWidth() < body.getSize()
                                .getWidth());
        assertTrue("Tooltip overflowed down", tooltip.getLocation().getY()
                + tooltip.getSize().getHeight() < body.getSize().getHeight());

    }

    protected void moveToRoot() {
        WebElement uiRoot = getDriver().findElement(By.vaadin("Root"));
        moveMouseToTopLeft(uiRoot);
    }

    protected WebElement getTooltip() throws InterruptedException {
        org.openqa.selenium.By tooltipBy = By.vaadin("Root/VTooltip[0]");
        return getDriver().findElement(tooltipBy);
    }

    protected void checkTooltipNotPresent() throws Exception {
        try {
            WebElement tooltip = getTooltip();
            if (!tooltip.getText().isEmpty()
                    || tooltip.getLocation().getX() > -999) {
                fail("Found tooltip that shouldn't be visible: "
                        + tooltip.getText() + " at " + tooltip.getLocation());
            }
        } catch (NoSuchElementException e) {
            fail("Tooltip element was removed completely, causing extra events to accessibility tools");
        }
    }

    protected void moveMouseToTopLeft(WebElement element) {
        moveMouseTo(element, 0, 0);
    }

    protected void moveMouseTo(WebElement element, int offsetX, int offsetY) {
        new Actions(getDriver()).moveToElement(element, offsetX, offsetY)
                .perform();
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingTooltip();
    }
}
