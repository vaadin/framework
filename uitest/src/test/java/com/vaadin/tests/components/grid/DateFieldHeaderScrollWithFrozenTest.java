package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateFieldHeaderScrollWithFrozenTest extends MultiBrowserTest {

    @Test
    public void iconNotVisibleWhenScrolled() throws InterruptedException {
        openTestURL();

        waitForElementPresent(By.className("v-datefield-button"));

        GridElement gridElement = $(GridElement.class).first();
        WebElement buttonElement = findElement(
                By.className("v-datefield-button"));
        GridCellElement frozenElement = gridElement.getHeaderCell(1, 1);

        gridElement.getHorizontalScroller().scrollLeft(60);
        sleep(100); // wait for scrolling to finish

        int buttonRight = buttonElement.getLocation().getX()
                + buttonElement.getRect().getWidth();
        int frozenRight = frozenElement.getLocation().getX()
                + frozenElement.getRect().getWidth();
        assertTrue(buttonRight + " is not smaller than " + frozenRight
                + ", not enough scrolling", buttonRight < frozenRight);

        Integer buttonZ = Integer.valueOf(buttonElement.getCssValue("z-index"));
        Integer frozenZ = Integer.valueOf(frozenElement.getCssValue("z-index"));
        assertTrue(
                buttonZ + " is not smaller than " + frozenZ
                        + ", button not hidden beneath frozen column",
                buttonZ < frozenZ);
    }
}
