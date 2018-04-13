package com.vaadin.tests.components.grid;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridReorderColumnsTest extends MultiBrowserTest {

    @Test
    public void testEmptyGrid() {
        openTestURL();

        testReordering("emptyGrid");
    }

    @Test
    public void testContentGrid() {
        openTestURL();

        testReordering("contentGrid");
    }

    private void testReordering(String id) {
        GridElement grid = $(GridElement.class).id(id);
        GridCellElement headerCell1 = grid.getHeaderCellByCaption("caption1");
        GridCellElement headerCell3 = grid.getHeaderCellByCaption("caption3");

        assertThat(grid.getHeaderCell(0, 0), is(headerCell1));

        new Actions(getDriver()).clickAndHold(headerCell1)
                .moveToElement(headerCell3, 5, 0).release().perform();

        waitForElementNotPresent(By.className("dragged-column-header"));

        assertThat(grid.getHeaderCell(0, 0), not(is(headerCell1)));
    }
}
