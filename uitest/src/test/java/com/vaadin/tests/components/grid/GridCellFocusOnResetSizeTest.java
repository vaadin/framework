package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elementsbase.ServerClass;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridCellFocusOnResetSizeTest extends MultiBrowserTest {

    @ServerClass("com.vaadin.tests.widgetset.server.TestWidgetComponent")
    public static class MyGridElement extends GridElement {
    }

    @Test
    public void testCellFocusOnSizeReset() throws IOException {
        openTestURL();

        GridElement grid = $(MyGridElement.class).first();
        int rowIndex = 9;
        grid.getCell(rowIndex, 0).click();
        assertTrue("Row was not focused after click.",
                grid.getRow(rowIndex).isFocused());

        // Clicking the button decreases size until it is down to 5 rows.
        while (rowIndex > 4) {
            findElement(By.tagName("button")).click();
            assertTrue("Row focus was not moved when size decreased",
                    grid.getRow(--rowIndex).isFocused());
        }

        // Next click increases size back to 10, this should not move focus.
        findElement(By.tagName("button")).click();
        assertTrue("Row focus should not have moved when size increased",
                grid.getRow(4).isFocused());
    }
}
