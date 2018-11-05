package com.vaadin.tests.tooltip;

import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.TooltipTest;

/**
 *
 *
 * @author Vaadin Ltd
 */
public class DragAndDropWrapperTooltipsTest extends TooltipTest {
    @Test
    public void testDragAndDropTooltips() throws Exception {
        openTestURL();
        LabelElement element = $(LabelElement.class).get(4);
        LabelElement targetElement = $(LabelElement.class).get(1);
        checkTooltip(element,
                "Tooltip for the wrapper wrapping all the draggable layouts");
        new Actions(getDriver()).clickAndHold(element)
                .moveToElement(targetElement).perform();
        sleep(500);
        checkTooltipNotPresent();
        new Actions(getDriver()).release().perform();
        checkTooltip(element, "Drag was performed and tooltip was changed");
    }
}
