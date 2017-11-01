package com.vaadin.tests.components.grid;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridSvgInCellTest extends SingleBrowserTest {

    @Before
    public void before() {
        setDebug(true);
        openTestURL();
    }

    @Test
    public void moveMouseOverSvgInCell() {
        GridElement grid = $(GridElement.class).first();
        new Actions(driver).moveToElement(grid.getCell(0, 0)).perform();
        assertNoErrorNotifications();
    }
}
