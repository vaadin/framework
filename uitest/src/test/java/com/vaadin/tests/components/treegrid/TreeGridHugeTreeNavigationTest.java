package com.vaadin.tests.components.treegrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TreeGridHugeTreeNavigationTest extends MultiBrowserTest {

    private TreeGridElement grid;

    @Before
    public void before() {
        setDebug(true);
        openTestURL();
        grid = $(TreeGridElement.class).first();
    }

    @Test
    public void keyboard_navigation() {
        grid.getRow(0).getCell(0).click();

        // Should navigate to "Granddad 1" and expand it
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.RIGHT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1",
                "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2");
        checkRowFocused(1);

        // Should navigate to and expand "Dad 1/1"
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.DOWN, Keys.RIGHT)
                .perform();
        assertCellTexts(0, 0,
                "Granddad 0", "Granddad 1", "Dad 1/0", "Dad 1/1",
                "Son 1/1/0", "Son 1/1/1", "Son 1/1/2", "Son 1/1/3");
        checkRowFocused(3);

        // Should navigate 100 items down
        Keys downKeyArr[] = new Keys[100];
        for (int i = 0; i < 100; i++) {
            downKeyArr[i] = Keys.DOWN;
        }
        new Actions(getDriver()).sendKeys(downKeyArr).perform();

        WebElement son1_1_99 = findFocusedRow();
        assertEquals("Son 1/1/99 --", son1_1_99.getText());

        // Should navigate to "Dad 1/1" back
        new Actions(getDriver())
                .sendKeys(Keys.HOME, Keys.DOWN, Keys.DOWN, Keys.DOWN).perform();
        WebElement dad1_1 = findFocusedRow();
        assertEquals("Dad 1/1 --", dad1_1.getText());

        // Should collapse "Dad 1/1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1",
                "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2");
        checkRowFocused(3);

        // Should navigate to "Granddad 1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1",
                "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2");
        checkRowFocused(1);

        // Should collapse "Granddad 1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Granddad 2");
        checkRowFocused(1);

        // Nothing should happen
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, "Granddad 0", "Granddad 1", "Granddad 2");
        checkRowFocused(1);
        assertNoErrorNotifications();
    }

    @Test
    public void no_exception_when_calling_expand_or_collapse_twice() {

        // Currently the collapsed state is updated in a round trip to the
        // server, thus it is possible to trigger an expand on the same row
        // multiple times through the UI. This should not cause exceptions, but
        // rather ignore the redundant calls.

        grid.getRow(0).getCell(0).click();
        new Actions(getDriver()).sendKeys(Keys.RIGHT, Keys.RIGHT).perform();
        assertNoErrorNotifications();
        new Actions(getDriver()).sendKeys(Keys.LEFT, Keys.LEFT).perform();
        assertNoErrorNotifications();
    }

    @Test
    public void uncollapsible_item() {
        grid.getRow(0).getCell(0).click();
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.DOWN, Keys.RIGHT).perform();
        grid.waitForVaadin();
        //expand Dad 2/1
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.DOWN, Keys.RIGHT).perform();
        grid.waitForVaadin();
        assertNoErrorNotifications();
        assertCellTexts(5,0,"Son 2/1/0");
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        grid.waitForVaadin();
        assertNoErrorNotifications();
        assertCellTexts(5,0,"Son 2/1/0");
    }
    @Test
    public void can_toggle_collapse_on_row_that_is_no_longer_in_cache() {
        grid.getRow(0).getCell(0).click();

        // Expand 2 levels
        new Actions(getDriver()).sendKeys(Keys.RIGHT).perform();
        grid.waitForVaadin();
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.RIGHT).perform();
        grid.waitForVaadin();
        grid.scrollToRow(200);
        grid.waitForVaadin();
        //Jump into view
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        grid.waitForVaadin();
        //Collapse
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        grid.waitForVaadin();
        assertEquals(6, grid.getRowCount());

        // Expand
        new Actions(getDriver()).sendKeys(Keys.RIGHT, Keys.UP).perform();
        grid.waitForVaadin();
        grid.scrollToRow(200);
        new Actions(getDriver()).sendKeys(Keys.RIGHT).perform();
        grid.waitForVaadin();
        assertEquals(306, grid.getRowCount());
    }

    private WebElement findFocusedRow() {
        return grid.findElement(By.className("v-treegrid-row-focused"));
    }

    private void checkRowFocused(int index) {
        if (index > 0) {
            assertFalse(grid.getRow(index - 1)
                    .hasClassName("v-treegrid-row-focused"));
    }
        assertTrue(grid.getRow(index)
                .hasClassName("v-treegrid-row-focused"));
        assertFalse(grid.getRow(index + 1)
                .hasClassName("v-treegrid-row-focused"));
    }

    private void assertCellTexts(int startRowIndex, int cellIndex,
            String... cellTexts) {
        int index = startRowIndex;
        for (String cellText : cellTexts) {
            assertEquals(cellText,
                    grid.getRow(index).getCell(cellIndex).getText());
            index++;
        }
    }
}
