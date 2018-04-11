package com.vaadin.tests.components.table;

import static com.vaadin.tests.components.table.ExpandingContainerVisibleRowRaceCondition.TABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class ExpandingContainerVisibleRowRaceConditionTest
        extends MultiBrowserTest {

    private static final int ROW_HEIGHT = 20;

    @Test
    public void testScrollingWorksWithoutJumpingWhenItemSetChangeOccurs() {
        openTestURL();
        sleep(1000);

        WebElement table = vaadinElementById(TABLE);
        assertFirstRowIdIs("ROW #120");

        testBenchElement(table.findElement(By.className("v-scrollable")))
                .scroll(320 * ROW_HEIGHT);
        sleep(1000);

        assertRowIdIsInThePage("ROW #330");
        assertScrollPositionIsNotVisible();
    }

    private void assertFirstRowIdIs(String expected) {
        List<WebElement> cellsOfFirstColumn = getCellsOfFirstColumn();
        WebElement first = cellsOfFirstColumn.get(0);
        assertEquals(expected, first.getText());
    }

    private void assertRowIdIsInThePage(String expected) {
        List<WebElement> cellsOfFirstColumn = getCellsOfFirstColumn();
        for (WebElement rowId : cellsOfFirstColumn) {
            if (expected.equals(rowId.getText())) {
                return;
            }
        }
        fail("Expected row was not found");
    }

    private void assertScrollPositionIsNotVisible() {
        WebElement table = vaadinElementById(TABLE);
        WebElement scrollPosition = table
                .findElement(By.className("v-table-scrollposition"));
        assertFalse(scrollPosition.isDisplayed());
    }

    private List<WebElement> getCellsOfFirstColumn() {
        WebElement table = vaadinElementById(TABLE);
        List<WebElement> firstCellOfRows = table
                .findElements(By.cssSelector(".v-table-table tr > td"));
        return firstCellOfRows;
    }
}
