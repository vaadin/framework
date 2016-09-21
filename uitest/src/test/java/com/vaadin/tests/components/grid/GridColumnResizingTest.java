package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridColumnResizingTest extends MultiBrowserTest {

    @Test
    public void serverSetWidth() {
        openTestURL();

        serverSideSetWidth(50);
        assertColumnWidth(50, 0);

        serverSideSetWidth(500);
        assertColumnWidth(500, 0);
    }

    @Test
    public void setResizable() {
        openTestURL();
        ButtonElement toggleResizableButton = $(ButtonElement.class).get(4);
        GridCellElement cell = getGrid().getHeaderCell(0, 0);

        Assert.assertEquals(true, cell.isElementPresent(
                By.cssSelector("div.v-grid-column-resize-handle")));

        toggleResizableButton.click();
        Assert.assertEquals(false, cell.isElementPresent(
                By.cssSelector("div.v-grid-column-resize-handle")));
    }

    @Test
    public void setExpandRatio() {
        openTestURL();
        ButtonElement setExpandRatioButton = $(ButtonElement.class).get(1);

        setExpandRatioButton.click();
        assertColumnWidthWithThreshold(375, 0, 2);
        assertColumnWidthWithThreshold(125, 1, 2);
    }

    @Test
    public void setMinimumWidth() {
        openTestURL();

        setMinWidth(100);
        serverSideSetWidth(50);
        assertColumnWidth(100, 0);

        serverSideSetWidth(150);
        dragResizeColumn(0, 0, -100);
        assertColumnWidth(100, 0);
    }

    @Test
    public void setMaximumWidth() {
        openTestURL();

        serverSideSetWidth(50);
        setMaxWidth(100);

        serverSideSetWidth(150);
        assertColumnWidth(100, 0);

        // TODO add the following when grid column width recalculation has been
        // fixed in the case where the sum of column widths exceeds the visible
        // area

        // serverSideSetWidth(50);
        // dragResizeColumn(0, 0, 200);
        // assertColumnWidth(100, 0);
    }

    @Test
    public void resizeEventListener() {
        openTestURL();

        Assert.assertEquals("not resized",
                $(LabelElement.class).get(1).getText());

        serverSideSetWidth(150);
        Assert.assertEquals("server resized",
                $(LabelElement.class).get(1).getText());

        dragResizeColumn(0, 0, 100);
        Assert.assertEquals("client resized",
                $(LabelElement.class).get(1).getText());
    }

    private GridElement getGrid() {
        return $(GridElement.class).first();
    }

    private void serverSideSetWidth(double width) {
        TextFieldElement textField = $(TextFieldElement.class).first();
        ButtonElement setWidthButton = $(ButtonElement.class).get(0);
        textField.clear();
        textField.sendKeys(String.valueOf(width), Keys.ENTER);
        setWidthButton.click();
    }

    private void setMinWidth(double minWidth) {
        TextFieldElement textField = $(TextFieldElement.class).first();
        ButtonElement setMinWidthButton = $(ButtonElement.class).get(2);
        textField.clear();
        textField.sendKeys(String.valueOf(minWidth), Keys.ENTER);
        setMinWidthButton.click();
    }

    private void setMaxWidth(double maxWidth) {
        TextFieldElement textField = $(TextFieldElement.class).first();
        ButtonElement setMaxWidthButton = $(ButtonElement.class).get(3);
        textField.clear();
        textField.sendKeys(String.valueOf(maxWidth), Keys.ENTER);
        setMaxWidthButton.click();
    }

    private void dragResizeColumn(int columnIndex, int posX, int offset) {
        GridCellElement headerCell = getGrid().getHeaderCell(0, columnIndex);
        Dimension size = headerCell.getSize();
        new Actions(getDriver())
                .moveToElement(headerCell, size.getWidth() + posX,
                        size.getHeight() / 2)
                .clickAndHold().moveByOffset(offset, 0).release().perform();
    }

    private void assertColumnWidth(int width, int columnIndex) {
        Assert.assertEquals(width,
                getGrid().getCell(0, columnIndex).getSize().getWidth());
    }

    private void assertColumnWidthWithThreshold(int width, int columnIndex,
            int threshold) {
        Assert.assertTrue(
                Math.abs(getGrid().getCell(0, columnIndex).getSize().getWidth()
                        - width) <= threshold);
    }
}
