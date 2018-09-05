package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridResizeAndScrollTest extends MultiBrowserTest {

    @Test
    public void scrollAndClick() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(49);
        // select a row (click on checkbox)
        grid.getCell(49, 0).click();

        // verify rows are what they should be
        GridCellElement cell = grid.getCell(33, 1);
        String textBefore = cell.getText();
        cell.click();

        assertEquals("String contents changed on click", textBefore,
                cell.getText());

    }

}
