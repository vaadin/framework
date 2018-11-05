package com.vaadin.tests.tb3;

import org.junit.Ignore;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Base class for TestBench 3+ tests that use DnD. This class contains utility
 * methods for DnD operations.
 *
 * @author Vaadin Ltd
 */
@Ignore
public abstract class DndActionsTest extends MultiBrowserTest {

    public void dragAndDrop(WebElement element, int xOffset, int yOffset) {
        Actions action = new Actions(getDriver());
        action.dragAndDropBy(element, xOffset, yOffset);
        action.build().perform();
    }

    public void dragAndDrop(WebElement element, WebElement target) {
        Actions action = new Actions(getDriver());
        action.dragAndDrop(element, target);
        action.build().perform();
    }
}
