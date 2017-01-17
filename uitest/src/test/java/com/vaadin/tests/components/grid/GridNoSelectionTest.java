package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.components.grid.basics.GridBasicsTest;

public class GridNoSelectionTest extends GridBasicsTest {

    @Test
    public void clickToSelectDoesNothing() {
        setSelectionModelNone();

        verifyClickSelectDoesNothing();
    }

    @Test
    public void spaceBarSelectDoesNothing() {
        setSelectionModelNone();

        verifyKeyboardSelectionNotAllowed();
    }

    @Test
    public void serverSideSelectDoesNothing() {
        toggleFirstRowSelection();

        assertTrue(getGridElement().getRow(0).isSelected());

        setSelectionModelNone();

        toggleFirstRowSelection();

        assertFalse(getGridElement().getRow(0).isSelected());
    }

    @Test
    public void changingSelectionModels_fromMulti() {
        setSelectionModelMulti();

        getGridElement().getCell(0, 0).click();
        assertTrue(getGridElement().getRow(0).isSelected());

        getGridElement().scrollToRow(50);
        getGridElement().getCell(49, 0).click();
        assertTrue(getGridElement().getRow(49).isSelected());

        setSelectionModelNone();

        assertFalse(getGridElement().getRow(0).isSelected());

        verifyClickSelectDoesNothing();
        verifyKeyboardSelectionNotAllowed();

        getGridElement().scrollToRow(50);
        assertFalse(getGridElement().getRow(49).isSelected());
    }

    @Test
    public void changingSelectionModels_fromMultiAllSelected() {
        setSelectionModelMulti();

        getGridHeaderRowCells().get(0).click(); // select all click

        assertTrue(getDefaultColumnHeader(0).findElement(By.tagName("input"))
                .isSelected());
        assertTrue(getGridElement().getRow(0).isSelected());
        assertTrue(getGridElement().getRow(1).isSelected());
        assertTrue(getGridElement().getRow(10).isSelected());

        setSelectionModelNone();

        assertEquals(0, getDefaultColumnHeader(0)
                .findElements(By.tagName("input")).size());
        assertFalse(getGridElement().getRow(0).isSelected());
        assertFalse(getGridElement().getRow(1).isSelected());
        assertFalse(getGridElement().getRow(10).isSelected());
    }

    @Test
    public void changingSelectionModels_fromSingle() {
        // this is the same as default
        getGridElement().getCell(3, 0).click();
        assertTrue(getGridElement().getRow(3).isSelected());

        setSelectionModelNone();

        assertFalse(getGridElement().getRow(3).isSelected());

        verifyClickSelectDoesNothing();
        verifyKeyboardSelectionNotAllowed();
    }

    protected void verifyClickSelectDoesNothing() {
        getGridElement().getCell(0, 0).click();

        assertFalse("Grid row should not be selected",
                getGridElement().getRow(0).isSelected());

        getGridElement().getCell(2, 2).click();

        assertFalse("Grid row should not be selected",
                getGridElement().getRow(2).isSelected());
    }

    protected void verifyKeyboardSelectionNotAllowed() {
        GridElement grid = getGridElement();
        grid.getCell(3, 1).click();

        assertFalse("Grid should not allow selecting",
                grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertFalse("Grid should not allow selecting with space key",
                grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();

        assertFalse("Grid should not allow selecting",
                grid.getRow(3).isSelected());

        new Actions(getDriver()).sendKeys(Keys.ARROW_DOWN).perform();
        new Actions(getDriver()).sendKeys(Keys.SPACE).perform();
        assertFalse("Grid should not allow selecting",
                grid.getRow(4).isSelected());
    }

}
