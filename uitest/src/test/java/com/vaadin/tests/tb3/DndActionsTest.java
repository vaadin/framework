package com.vaadin.tests.tb3;

import org.junit.Ignore;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.parallel.BrowserUtil;

/**
 * Base class for TestBench 3+ tests that use DnD. This class contains utility
 * methods for DnD operations.
 *
 * @author Vaadin Ltd
 */
@Ignore
public abstract class DndActionsTest extends MultiBrowserTest {

    public void dragAndDrop(WebElement element, int xOffset, int yOffset) {
        /*
         * Selenium doesn't properly drag and drop items in IE8. It tries to
         * start dragging an element from a position above the element itself.
         */
        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            Actions action = new Actions(getDriver());
            action.moveToElement(element);
            action.moveByOffset(0, 1);
            action.clickAndHold();
            action.moveByOffset(xOffset, yOffset);
            action.release();
            action.build().perform();
        } else {
            Actions action = new Actions(getDriver());
            action.dragAndDropBy(element, xOffset, yOffset);
            action.build().perform();
        }
    }

    public void dragAndDrop(WebElement element, WebElement target) {
        /*
         * Selenium doesn't properly drag and drop items in IE8. It tries to
         * start dragging an element from a position above the element itself.
         */
        if (BrowserUtil.isIE8(getDesiredCapabilities())) {
            Actions action = new Actions(getDriver());
            action.moveToElement(element);
            action.moveByOffset(0, 1);
            action.clickAndHold();
            action.moveToElement(target);
            action.release();
            action.build().perform();
        } else {
            Actions action = new Actions(getDriver());
            action.dragAndDrop(element, target);
            action.build().perform();
        }
    }
}
