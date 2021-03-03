package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridEditorCheckBoxTest extends MultiBrowserTest {

    @Test
    public void testPositions() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // open editor for second row
        grid.getCell(1, 1).doubleClick();
        waitForElementPresent(By.className("v-grid-editor"));

        // regular row cells
        List<WebElement> rowCells = grid.getRow(0)
                .findElements(By.className("v-grid-cell"));

        // editor cells are divided in two groups, first one for frozen columns,
        // second one for the rest
        List<WebElement> editorCellGroups = grid
                .findElements(By.className("v-grid-editor-cells"));
        int column = 0;
        for (WebElement editorCellGroup : editorCellGroups) {
            // find the actual editor cells (no shared class name)
            List<WebElement> editorCells = editorCellGroup
                    .findElements(By.xpath("./child::*"));
            for (WebElement editorCell : editorCells) {

                // find the margin within the editor row
                List<WebElement> checkBoxElements = editorCell
                        .findElements(By.className("v-checkbox"));
                WebElement editorInput;
                if (checkBoxElements.isEmpty()) {
                    // use the actual input element for position check
                    editorInput = editorCell.findElement(By.tagName("input"));
                } else {
                    // v-checkbox positions a fake input element
                    editorInput = checkBoxElements.get(0);
                }
                int editorMargin = editorInput.getLocation().getX()
                        - editorCell.getLocation().getX();

                // find the margin within the regular row
                WebElement rowCell = rowCells.get(column);
                int comparisonMargin;
                if (column == 1 || column == 2) {
                    // these columns have text content on regular rows, margin
                    // is created with padding
                    String padding = rowCell.getCssValue("padding-left");
                    comparisonMargin = Integer.valueOf(
                            padding.substring(0, padding.indexOf("px")));
                } else {
                    WebElement rowContent = rowCell
                            .findElement(By.tagName("input"));
                    comparisonMargin = rowContent.getLocation().getX()
                            - rowCell.getLocation().getX();
                }

                // ensure that the editor input position matches the regular row
                // content position
                assertThat(
                        "Unexpected input location for column " + column
                                + " editor",
                        (double) editorMargin, closeTo(comparisonMargin, 1d));
                ++column;
            }
        }
        assertEquals("Unexpect amount of checked columns,", 5, column);
    }
}
