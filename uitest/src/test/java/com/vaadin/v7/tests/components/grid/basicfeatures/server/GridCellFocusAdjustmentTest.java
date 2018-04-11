package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class GridCellFocusAdjustmentTest extends GridBasicFeaturesTest {

    @Test
    public void testCellFocusWithAddAndRemoveRows() {
        openTestURL();
        GridElement grid = getGridElement();

        grid.getCell(0, 0).click();

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Cell focus was not moved when adding a row",
                grid.getCell(1, 0).isFocused());

        selectMenuPath("Component", "Body rows", "Add 18 rows");
        assertTrue("Cell focus was not moved when adding multiple rows",
                grid.getCell(19, 0).isFocused());

        for (int i = 18; i <= 0; --i) {
            selectMenuPath("Component", "Body rows", "Remove first row");
            assertTrue("Cell focus was not moved when removing a row",
                    grid.getCell(i, 0).isFocused());
        }
    }

    @Test
    public void testCellFocusOffsetWhileInDifferentSection() {
        openTestURL();
        getGridElement().getCell(0, 0).click();
        new Actions(getDriver()).sendKeys(Keys.UP).perform();
        assertTrue("Header 0,0 should've become focused",
                getGridElement().getHeaderCell(0, 0).isFocused());

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Header 0,0 should've remained focused",
                getGridElement().getHeaderCell(0, 0).isFocused());
    }

    @Test
    public void testCellFocusOffsetWhileInSameSectionAndInsertedAbove() {
        openTestURL();
        assertTrue("Body 0,0 should've gotten focus",
                getGridElement().getCell(0, 0).isFocused());

        selectMenuPath("Component", "Body rows", "Add first row");
        assertTrue("Body 1,0 should've gotten focus",
                getGridElement().getCell(1, 0).isFocused());
    }

    @Test
    public void testCellFocusOffsetWhileInSameSectionAndInsertedBelow() {
        openTestURL();
        assertTrue("Body 0,0 should've gotten focus",
                getGridElement().getCell(0, 0).isFocused());

        selectMenuPath("Component", "Body rows", "Add third row");
        assertTrue("Body 0,0 should've remained focused",
                getGridElement().getCell(0, 0).isFocused());
    }

}
