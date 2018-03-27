package com.vaadin.tests.components.grid;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridResizeHiddenColumnTest extends MultiBrowserTest {

    @Before
    public void before() {
        openTestURL();
    }

    @Test
    public void testDragResizeHiddenColumnSize() {
        GridElement grid = $(GridElement.class).first();
        Actions action = new Actions(getDriver());

        // Check if column 'Gender' hidden
        List<GridCellElement> headerCells = grid.getHeaderCells(0);
        Assert.assertEquals("There should be two visible columns", 2,
                headerCells.size());
        Assert.assertFalse("Gender column should be hidden",
                containsText("Gender", headerCells));

        // Resize first column
        int dragOffset = -100;
        int headerCellWidth = headerCells.get(0).getSize().getWidth();
        dragResizeColumn(headerCells.get(0), 1, dragOffset);

        // When dragging the resizer on IE8, the final offset will be smaller
        // (might be an issue with the feature that doesn't start resizing until
        // the cursor moved a few pixels)
        double delta = BrowserUtil.isIE8(getDesiredCapabilities()) ? 5d : 0;
        Assert.assertEquals(
                "Column width should've changed by " + dragOffset + "px",
                headerCellWidth + dragOffset,
                headerCells.get(0).getSize().getWidth(), delta);

        // Make column 'Gender' visible
        WebElement menuButton = grid.findElement(By.className("v-contextmenu"))
                .findElement(By.tagName("button"));
        action.click(menuButton).perform(); // Click on menu button

        WebElement sidebarPopup = findElement(
                By.className("v-grid-sidebar-popup"));
        WebElement visibilityToggle = findElementByText("Gender",
                sidebarPopup.findElements(By.className("gwt-MenuItem")));
        action.click(visibilityToggle).perform(); // Click on 'Gender' menu item

        // Check if column 'Gender' is visible
        headerCells = grid.getHeaderCells(0);
        Assert.assertEquals("There should be three visible columns", 3,
                headerCells.size());
        Assert.assertTrue("Gender column should be visible",
                containsText("Gender", headerCells));

        // Check if column 'Gender' has expanded width
        int widthSum = 0;
        for (GridCellElement e : headerCells) {
            widthSum += e.getSize().getWidth();
        }
        Assert.assertEquals("Gender column should take up the remaining space",
                grid.getHeader().getSize().getWidth(), widthSum, 1d);
    }

    private WebElement findElementByText(String text,
            List<? extends WebElement> elements) {
        for (WebElement e : elements) {
            if (text.equalsIgnoreCase(e.getText())) {
                return e;
            }
        }
        return null;
    }

    private boolean containsText(String text,
            List<? extends WebElement> elements) {
        return !(findElementByText(text, elements) == null);
    }

    private void dragResizeColumn(GridCellElement headerCell, int posX,
            int offset) {
        Dimension size = headerCell.getSize();
        new Actions(getDriver())
                .moveToElement(headerCell, size.getWidth() + posX,
                        size.getHeight() / 2)
                .clickAndHold().moveByOffset(offset, 0).release().perform();
    }
}
