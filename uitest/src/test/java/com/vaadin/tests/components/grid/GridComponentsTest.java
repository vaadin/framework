package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Locale;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Assume;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.BrowserUtil;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridComponentsTest extends MultiBrowserTest {

    @Test
    public void testReuseTextFieldOnScroll() {
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
        grid.getCell(1, 0).click();

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

    @Test
    public void testRowNotSelectedFromHeaderOrFooter() {
        openTestURL();

        GridElement grid = $(GridElement.class).first();
        grid.getCell(4, 0).$(LabelElement.class).first().click(10, 10,
                new Keys[0]);
        assertTrue("Row 4 should be selected", grid.getRow(4).isSelected());

        TextFieldElement headerTextField = grid.getHeaderCell(1, 0)
                .$(TextFieldElement.class).first();
        headerTextField.sendKeys(Keys.SPACE);

        assertFalse("Row 1 should not be selected",
                grid.getRow(1).isSelected());
        assertTrue("Row 4 should still be selected",
                grid.getRow(4).isSelected());

        TextFieldElement footerTextField = grid.getFooterCell(0, 0)
                .$(TextFieldElement.class).first();
        footerTextField.sendKeys(Keys.SPACE);

        assertFalse("Row 0 should not be selected",
                grid.getRow(0).isSelected());
        assertTrue("Row 4 should still be selected",
                grid.getRow(4).isSelected());

    }

    @Test
    public void testTabNavigation() {
        Assume.assumeFalse("Firefox has issues with Shift",
                BrowserUtil.isFirefox(getDesiredCapabilities()));

        openTestURL();

        GridElement grid = $(GridElement.class).first();
        WebElement resizeHandle = grid.getHeaderCell(0, 0)
                .findElement(By.cssSelector("div.v-grid-column-resize-handle"));

        new Actions(getDriver()).moveToElement(resizeHandle).clickAndHold()
                .moveByOffset(440, 0).release().perform();

        // Scroll to end
        grid.getCell(0, 2);
        int scrollMax = getScrollLeft(grid);
        assertTrue("Width of the grid too narrow, no scroll bar",
                scrollMax > 0);

        // Scroll to start
        grid.getHorizontalScroller().scrollLeft(0);

        assertEquals(
                "Grid should scrolled to the start for this part of the test..",
                0, getScrollLeft(grid));

        // Focus TextField in second column
        WebElement textField = grid.getCell(0, 1)
                .findElement(By.tagName("input"));
        textField.click();

        // Navigate to currently out of viewport Button
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        assertEquals("Grid should be scrolled to the end", scrollMax,
                getScrollLeft(grid));

        // Navigate back to fully visible TextField
        pressKeyWithModifier(Keys.SHIFT, Keys.TAB);
        assertEquals(
                "Grid should not scroll when focusing the text field again. ",
                scrollMax, getScrollLeft(grid));

        // Navigate to out of viewport TextField in Header
        pressKeyWithModifier(Keys.SHIFT, Keys.TAB);

        assertEquals("Focus should be in TextField in Header", "headerField",
                getFocusedElement().getAttribute("id"));
        assertEquals("Grid should've scrolled back to start.", 0,
                getScrollLeft(grid));

        // Focus button in last visible row of Grid
        grid.getCell(6, 2).findElement(By.id("row_6")).click();

        // Navigate to currently out of viewport TextField on Row 7
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        int scrollTopRow7 = Integer
                .parseInt(grid.getVerticalScroller().getAttribute("scrollTop"));
        assertTrue("Grid should be scrolled to show row 7", scrollTopRow7 > 0);

        // Navigate to currently out of viewport TextField on Row 8
        new Actions(getDriver()).sendKeys(Keys.TAB, Keys.TAB).perform();
        assertTrue("Grid should be scrolled to show row 8",
                Integer.parseInt(grid.getVerticalScroller()
                        .getAttribute("scrollTop")) > scrollTopRow7);

        // Focus button in first visible row of Grid
        grid.getCell(2, 2).findElement(By.id("row_2")).click();
        int scrollTopRow2 = Integer
                .parseInt(grid.getVerticalScroller().getAttribute("scrollTop"));

        // Navigate to currently out of viewport Button on Row 1
        pressKeyWithModifier(Keys.SHIFT, Keys.TAB);
        pressKeyWithModifier(Keys.SHIFT, Keys.TAB);
        int scrollTopRow1 = Integer
                .parseInt(grid.getVerticalScroller().getAttribute("scrollTop"));
        assertTrue("Grid should be scrolled to show row 1",
                scrollTopRow1 < scrollTopRow2);

        // Continue further to the very first row
        pressKeyWithModifier(Keys.SHIFT, Keys.TAB);
        pressKeyWithModifier(Keys.SHIFT, Keys.TAB);
        assertTrue("Grid should be scrolled to show row 0",
                Integer.parseInt(grid.getVerticalScroller()
                        .getAttribute("scrollTop")) < scrollTopRow1);

        // Focus button in last row of Grid
        grid.getCell(999, 2).findElement(By.id("row_999")).click();
        // Navigate to out of viewport TextField in Footer
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
        assertEquals("Focus should be in TextField in Footer", "footerField",
                getFocusedElement().getAttribute("id"));
        assertEquals("Grid should've scrolled horizontally back to start.", 0,
                getScrollLeft(grid));
    }

    private int getScrollLeft(GridElement grid) {
        return Integer.parseInt(
                grid.getHorizontalScroller().getAttribute("scrollLeft"));
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
        assertTrue("Notification should contain given text: " + string,
                $(NotificationElement.class).first().getText()
                        .contains(string));
        $(NotificationElement.class).first().close();
        waitUntil(driver -> !isElementPresent(NotificationElement.class), 10);
    }

    private void assertNoButton(int i) {
        GridRowElement row = $(GridElement.class).first().getRow(i);
        assertFalse("Row " + i + " should not have a button",
                row.getCell(2).isElementPresent(ButtonElement.class));
    }

    // Workaround for Chrome 75, sendKeys(Keys.chord(Keys.SHIFT, Keys.TAB))
    // doesn't work anymore
    private void pressKeyWithModifier(Keys keyModifier, Keys key) {
        new Actions(getDriver()).keyDown(keyModifier).perform();
        new Actions(getDriver()).sendKeys(key).perform();
        new Actions(getDriver()).keyUp(keyModifier).perform();
    }
}
