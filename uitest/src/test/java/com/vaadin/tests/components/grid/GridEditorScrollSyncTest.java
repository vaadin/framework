package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

public class GridEditorScrollSyncTest extends MultiBrowserTest {
    private GridElement grid;

    @Test
    public void testScrollAndEdit() {
        openTestURL();
        grid = $(GridElement.class).first();
        ((TestBenchElement) grid
                .findElement(By.className("v-grid-scroller-horizontal")))
                        .scrollLeft(300);
        openEditor();
        GridElement.GridCellElement rowCell = grid.getCell(1, 6);
        TestBenchElement editorField = grid.getEditor().getField(6);
        assertPosition(rowCell.getLocation().getX(),
                editorField.getWrappedElement().getLocation().getX());
    }

    private GridElement openEditor() {
        grid.getCell(0, 6).doubleClick();
        Assert.assertTrue("Grid editor should be displayed.",
                grid.getEditor().isDisplayed());
        return grid;
    }

    private void assertPosition(double expected, double actual) {
        // 1px leeway for calculations
        assertThat("Unexpected position.", expected, closeTo(actual, 1d));
    }
}
