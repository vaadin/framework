package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridComponentsTest extends MultiBrowserTest {

    @Test
    public void testReuseTextFieldOnScroll() {
        if (BrowserUtil.isPhantomJS(getDesiredCapabilities())) {
            // skip test on PhantomJS as it often crashes the browser
            return;
        }
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        editTextFieldInCell(grid, 0, 1);
        // Scroll out of view port
        grid.getRow(900);
        // Scroll back
        grid.getRow(0);

        WebElement textField = grid.getCell(0, 1)
                .findElement(By.tagName("input"));
        assertEquals("TextField value was reset", "Foo",
                textField.getAttribute("value"));
        assertTrue("No mention in the log",
                logContainsText("1. Reusing old text field for: Row 0"));
    }

    @Test
    public void testReuseTextFieldOnSelect() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        editTextFieldInCell(grid, 1, 1);
        // Select row
        grid.getCell(1, 1).click(1, 1);

        WebElement textField = grid.getCell(1, 1)
                .findElement(By.tagName("input"));
        assertEquals("TextField value was reset", "Foo",
                textField.getAttribute("value"));
        assertTrue("No mention in the log",
                logContainsText("1. Reusing old text field for: Row 1"));
    }

    @Test
    public void testReplaceData() {
        openTestURL();
        assertRowExists(5, "Row 5");
        $(ButtonElement.class).caption("Reset data").first().click();
        assertRowExists(5, "Row 1005");
    }

    @Test
    public void testTextFieldSize() {
        openTestURL();
        GridCellElement cell = $(GridElement.class).first().getCell(0, 1);
        int cellWidth = cell.getSize().getWidth();
        int fieldWidth = cell.findElement(By.tagName("input")).getSize()
                .getWidth();
        // padding left and right, +1 to fix sub pixel issues
        int padding = 18 * 2 + 1;

        int extraSpace = Math.abs(fieldWidth - cellWidth);
        assertTrue("Too much unused space in cell. Expected: " + padding
                + " Actual: " + extraSpace, extraSpace <= padding);
    }

    private void editTextFieldInCell(GridElement grid, int row, int col) {
        WebElement textField = grid.getCell(row, col)
                .findElement(By.tagName("input"));
        textField.clear();
        textField.sendKeys("Foo");
    }

    @Test
    public void testRow5() {
        openTestURL();
        assertRowExists(5, "Row 5");
    }

    @Test
    public void testRow0() {
        openTestURL();
        assertRowExists(0, "Row 0");
        assertEquals("Grid row height is not what it should be", 40,
                $(GridElement.class).first().getRow(0).getSize().getHeight());
    }

    @Test
    public void testRow999() {
        openTestURL();
        assertRowExists(999, "Row 999");
    }

    @Test
    public void testRow30() {
        openTestURL();
        Stream.of(30, 130, 230, 330).forEach(this::assertNoButton);
        IntStream.range(300, 310).forEach(this::assertNoButton);
    }

    @Test(expected = AssertionError.class)
    public void testRow31() {
        openTestURL();
        // There is a button on row 31. This should fail.
        assertNoButton(31);
    }

    @Test
    public void testHeaders() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridCellElement headerCell = grid.getHeaderCell(0, 0);
        assertTrue("First header should contain a Label",
                headerCell.isElementPresent(LabelElement.class));
        assertEquals("Label",
                headerCell.$(LabelElement.class).first().getText());
        assertFalse("Second header should not contain a component",
                grid.getHeaderCell(0, 1).isElementPresent(LabelElement.class));
        assertEquals("Other Components", grid.getHeaderCell(0, 1).getText());
    }

    @Test
    public void testSelectRowByClickingLabel() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        assertFalse("Row should not be initially selected",
                grid.getRow(0).isSelected());

        grid.getCell(0, 0).$(LabelElement.class).first().click(10, 10,
                new Keys[0]);
        assertTrue("Row should be selected", grid.getRow(0).isSelected());
    }

    private void assertRowExists(int i, String string) {
        GridRowElement row = $(GridElement.class).first().getRow(i);
        assertEquals("Label text did not match", string,
                row.getCell(0).getText());
        row.findElement(
                By.id(string.replace(' ', '_').toLowerCase(Locale.ROOT)))
                .click();
        // IE 11 is slow, need to wait for the notification.
        waitUntil(driver -> isElementPresent(NotificationElement.class), 10);
        assertTrue("Notification should contain given text",
                $(NotificationElement.class).first().getText()
                        .contains(string));
    }

    private void assertNoButton(int i) {
        GridRowElement row = $(GridElement.class).first().getRow(i);
        assertFalse("Row " + i + " should not have a button",
                row.getCell(2).isElementPresent(ButtonElement.class));
    }
}
