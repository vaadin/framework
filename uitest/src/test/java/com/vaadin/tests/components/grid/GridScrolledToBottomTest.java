package com.vaadin.tests.components.grid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.elements.VerticalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridScrolledToBottomTest extends MultiBrowserTest {

    @Test
    public void testResizingAndBack() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(99);

        GridRowElement row99 = grid.getRow(99);
        int rowHeight = row99.getSize().getHeight();
        assertThat(grid.getLocation().getY() + grid.getSize().getHeight(),
                greaterThan(row99.getLocation().getY() + rowHeight - 2));

        VerticalSplitPanelElement splitPanel = $(
                VerticalSplitPanelElement.class).first();
        TestBenchElement splitter = splitPanel.getSplitter();
        // resize by three rows
        Actions actions = new Actions(driver);
        actions.clickAndHold(splitter).moveByOffset(0, -rowHeight * 3).release()
                .perform();
        // resize back by two rows
        actions.clickAndHold(splitter).moveByOffset(0, rowHeight * 2).release()
                .perform();

        GridRowElement row95 = grid.getRow(95);
        GridRowElement row97 = grid.getRow(97);
        assertThat((double) row97.getLocation().getY(),
                greaterThan(row95.getLocation().getY() + rowHeight * 1.5));
    }

    @Test
    public void testResizingHalfRow() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.scrollToRow(99);

        GridRowElement row99 = grid.getRow(99);
        int rowHeight = row99.getSize().getHeight();
        int gridBottomY = grid.getLocation().getY()
                + grid.getSize().getHeight();

        // ensure that grid really is scrolled to bottom
        assertThat((double) gridBottomY,
                closeTo((double) row99.getLocation().getY() + rowHeight, 1d));

        VerticalSplitPanelElement splitPanel = $(
                VerticalSplitPanelElement.class).first();
        TestBenchElement splitter = splitPanel.getSplitter();
        // resize by half a row
        Actions actions = new Actions(driver);
        actions.clickAndHold(splitter).moveByOffset(0, -rowHeight / 2).release()
                .perform();
        // the last row is now only half visible, and in DOM tree it's actually
        // the first row now but positioned to the bottom

        // can't query grid.getRow(99) now or it moves the row position,
        // have to use element query instead
        List<WebElement> rows = grid.findElement(By.className("v-grid-body"))
                .findElements(By.className("v-grid-row"));
        WebElement firstRow = rows.get(0);
        WebElement lastRow = rows.get(rows.size() - 1);

        // ensure the scrolling didn't jump extra
        assertEquals("Person 99",
                firstRow.findElement(By.className("v-grid-cell")).getText());
        assertEquals("Person 98",
                lastRow.findElement(By.className("v-grid-cell")).getText());

        // re-calculate current end position
        gridBottomY = grid.getLocation().getY() + grid.getSize().getHeight();
        // ensure the correct final row really is only half visible at the
        // bottom
        assertThat(gridBottomY, greaterThan(firstRow.getLocation().getY()));
        assertThat(firstRow.getLocation().getY() + rowHeight,
                greaterThan(gridBottomY));
    }
}
