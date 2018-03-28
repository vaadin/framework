package com.vaadin.tests.components.grid;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class GridHeaderStyleNamesTest extends SingleBrowserTest {

    private GridElement grid;

    @Before
    public void findGridCells() {
        openTestURL();
        grid = $(GridElement.class).first();
    }

    private GridCellElement getMergedHeaderCell() {
        return grid.getHeaderCell(0, 3);
    }

    private GridCellElement getNameFooterCell() {
        return grid.getFooterCell(0, 2);
    }

    @Test
    public void cellStyleNamesCanBeAddedAndRemoved() {
        ButtonElement toggleStyles = $(ButtonElement.class)
                .caption("Toggle styles").first();

        assertStylesSet(true);
        toggleStyles.click();
        assertStylesSet(false);
        toggleStyles.click();
        assertStylesSet(true);
    }

    @Test
    public void rowStyleNamesCanBeAddedAndRemoved() {
        ButtonElement toggleStyles = $(ButtonElement.class)
                .caption("Toggle styles").first();

        assertRowStylesSet(true);
        toggleStyles.click();
        assertRowStylesSet(false);
        toggleStyles.click();
        assertRowStylesSet(true);

    }

    private void assertStylesSet(boolean set) {
        if (set) {
            assertHasStyleName(
                    "Footer cell should have the assigned 'name-footer' class name",
                    getNameFooterCell(), "name-footer");
            assertHasStyleName(
                    "Header cell should have the assigned 'name' class name",
                    getAgeHeaderCell(), "name");
            assertHasStyleName(
                    "The merged header cell should have the assigned 'city-country' class name",
                    getMergedHeaderCell(), "city-country");
        } else {
            assertHasNotStyleName(
                    "Footer cell should not have the removed 'name-footer' class name",
                    getNameFooterCell(), "name-footer");
            assertHasNotStyleName(
                    "Header cell should not have the removed 'name' class name",
                    getAgeHeaderCell(), "name");
            assertHasNotStyleName(
                    "Ther merged header cell should not have the removed 'city-country' class name",
                    getMergedHeaderCell(), "city-country");
        }
        assertHasStyleName(
                "The default v-grid-cell style name should not be removed from the header cell",
                getAgeHeaderCell(), "v-grid-cell");
        assertHasStyleName(
                "The default v-grid-cell style name should not be removed from the footer cell",
                getNameFooterCell(), "v-grid-cell");
        assertHasStyleName(
                "The default v-grid-cell style name should not be removed from the merged header cell",
                getMergedHeaderCell(), "v-grid-cell");

    }

    private void assertRowStylesSet(boolean set) {
        if (set) {
            assertHasStyleName(
                    "Footer row should have the assigned 'custom-row' class name",
                    getFooterRow(), "custom-row");
            assertHasStyleName(
                    "Header row should have the assigned 'custom-row' class name",
                    getHeaderRow(), "custom-row");
        } else {
            assertHasNotStyleName(
                    "Footer row should not have the removed 'custom-row' class name",
                    getFooterRow(), "custom-row");
            assertHasNotStyleName(
                    "Header row should not have the removed 'custom-row' class name",
                    getHeaderRow(), "custom-row");
        }
        assertHasStyleName(
                "The default v-grid-row style name should not be removed from the header row",
                getHeaderRow(), "v-grid-row");
        assertHasStyleName(
                "The default v-grid-row style name should not be removed from the footer row",
                getFooterRow(), "v-grid-row");

    }

    private WebElement getAgeHeaderCell() {
        return grid.getHeaderCell(1, 2);
    }

    private WebElement getFooterRow() {
        return grid.getFooterRow(0);
    }

    private WebElement getHeaderRow() {
        return grid.getHeaderRow(0);
    }

    private void assertHasStyleName(String message, WebElement element,
            String stylename) {
        if (!hasCssClass(element, stylename)) {
            fail(message);
        }
    }

    private void assertHasNotStyleName(String message, WebElement element,
            String stylename) {
        if (hasCssClass(element, stylename)) {
            fail(message);
        }
    }

}
