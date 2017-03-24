package com.vaadin.tests.components.treegrid;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TreeGridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.tb3.ParameterizedTB3Runner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ParameterizedTB3Runner.class)
@Ignore
public class TreeGridHugeTreeNavigationTest extends MultiBrowserTest {

    private TreeGridElement grid;

    @Before
    public void before() {
        setDebug(true);
        openTestURL("theme=valo");
        grid = $(TreeGridElement.class).first();
    }

    @Test
    public void keyboard_navigation() {
        grid.getRow(0).getCell(0).click();

        // Should navigate to "Granddad 1"  and expand it
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.RIGHT).perform();
        assertEquals(6, grid.getRowCount());
        assertCellTexts(0, 0, new String[]{"Granddad 0", "Granddad 1", "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2"});
        checkRowFocused(1);

        // Should navigate to and expand "Dad 1/1"
        new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.DOWN, Keys.RIGHT).perform();
        assertCellTexts(0, 0, new String[]{"Granddad 0", "Granddad 1",
                "Dad 1/0", "Dad 1/1",
                "Son 1/1/0", "Son 1/1/1", "Son 1/1/2", "Son 1/1/3"});
        checkRowFocused(3);

        // Should navigate 100 items down

        for (int i = 0; i < 50; i++) {
            new Actions(getDriver()).sendKeys(Keys.DOWN, Keys.DOWN).perform();
        }
        WebElement son1_1_99 = gindFocusedRow();
        assertEquals("Son 1/1/99 --", son1_1_99.getText());

        // Should navigate to "Dad 1/1" back
        new Actions(getDriver()).sendKeys(Keys.HOME, Keys.DOWN, Keys.DOWN, Keys.DOWN).perform();
        WebElement dad1_1 = gindFocusedRow();
        assertEquals("Dad 1/1 --", dad1_1.getText());

        // Should collapse "Dad 1/1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, new String[]{"Granddad 0", "Granddad 1",
                "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2"});
        checkRowFocused(4);

        // Should navigate to "Granddad 1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, new String[]{"Granddad 0", "Granddad 1",
                "Dad 1/0", "Dad 1/1", "Dad 1/2", "Granddad 2"});
        checkRowFocused(1);

        // Should collapse "Granddad 1"
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, new String[]{"Granddad 0", "Granddad 1", "Granddad 2"});
        checkRowFocused(1);

        // Nothing should happen
        new Actions(getDriver()).sendKeys(Keys.LEFT).perform();
        assertCellTexts(0, 0, new String[]{"Granddad 0", "Granddad 1", "Granddad 2"});
        checkRowFocused(1);
        assertNoErrorNotifications();
    }

    private WebElement gindFocusedRow() {
        return grid.findElement(By.className("v-grid-rowmode-row-focused"));
    }

    private void checkRowFocused(int index) {
        if (index > 0) {
            assertFalse(grid.getRow(index - 1).hasClassName("v-grid-rowmode-row-focused"));
        }
        assertTrue(grid.getRow(index).hasClassName("v-grid-rowmode-row-focused"));
        assertFalse(grid.getRow(index + 1).hasClassName("v-grid-rowmode-row-focused"));
    }


    private void assertCellTexts(int startRowIndex, int cellIndex,
                                 String[] cellTexts) {
        int index = startRowIndex;
        for (String cellText : cellTexts) {
            assertEquals(cellText,
                    grid.getRow(index).getCell(cellIndex).getText());
            index++;
        }
    }
}
