package com.vaadin.tests.components.grid;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class GridReorderHiddenColumnsJoinedFooterTest extends MultiBrowserTest {
    @Test
    public void test() {
        openTestURL();

        GridElement grid = $(GridElement.class).get(0);
        GridElement.GridCellElement headerCell1 = grid
                .getHeaderCellByCaption("caption1");
        GridElement.GridCellElement headerCell8 = grid
                .getHeaderCellByCaption("caption8");

        assertThat(grid.getHeaderCell(0, 0), is(headerCell1));

        new Actions(getDriver()).clickAndHold(headerCell1)
                .moveToElement(headerCell8, 2, 0).release().perform();

        waitForElementNotPresent(By.className("dragged-column-header"));

        assertThat(grid.getHeaderCell(0, 0), not(is(headerCell1)));
        assertThat(grid.getHeaderCell(0, 0).getText(), is("caption3"));
    }
}
