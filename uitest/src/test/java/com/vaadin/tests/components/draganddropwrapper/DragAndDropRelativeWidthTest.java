package com.vaadin.tests.components.draganddropwrapper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to check size of drag image element.
 *
 * @author Vaadin Ltd
 */
public class DragAndDropRelativeWidthTest extends MultiBrowserTest {

    @Test
    public void testDragImageElementSize() {
        openTestURL();

        WebElement label = getDriver().findElement(By.className("drag-source"));
        Dimension size = label.getSize();
        int height = size.getHeight();
        int width = size.getWidth();
        Actions actions = new Actions(getDriver());
        actions.moveToElement(label);
        actions.clickAndHold();
        actions.moveByOffset(100, 100);
        actions.build().perform();

        WebElement dragImage = getDriver()
                .findElement(By.className("v-drag-element"));

        assertEquals("Drag image element height is unexpected", height,
                dragImage.getSize().getHeight());
        assertEquals("Drag image element width is unexpected", width,
                dragImage.getSize().getWidth());
    }

}
