package com.vaadin.tests.components.table;

import static com.vaadin.tests.components.table.SelectAllRows.TOTAL_NUMBER_OF_ROWS;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test to see if all items of the table can be selected by selecting first row,
 * press shift then select last (#13008)
 *
 * @author Vaadin Ltd
 */
public class SelectAllRowsTest extends MultiBrowserTest {

    @Override
    protected boolean requireWindowFocusForIE() {
        return true;
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersSupportingShiftClick();
    }

    @Test
    public void testAllRowsAreSelected() {
        openTestURL();

        clickFirstRow();
        scrollTableToBottom();
        clickLastRow();

        assertEquals(TOTAL_NUMBER_OF_ROWS, countSelectedItems());
    }

    protected void clickFirstRow() {
        getVisibleTableRows().get(0).click();
    }

    private void clickLastRow() {
        List<WebElement> rows = getVisibleTableRows();
        shiftClickElement(rows.get(rows.size() - 1));
    }

    protected void shiftClickElement(WebElement element) {
        new Actions(getDriver()).keyDown(Keys.SHIFT).click(element)
                .keyUp(Keys.SHIFT).perform();
    }

    private int countSelectedItems() {
        $(ButtonElement.class).first().click();
        String count = $(LabelElement.class).get(1).getText();
        return Integer.parseInt(count);
    }

    private TableElement getTable() {
        return $(TableElement.class).first();
    }

    private void scrollTableToBottom() {
        testBenchElement(getTable().findElement(By.className("v-scrollable")))
                .scroll(TOTAL_NUMBER_OF_ROWS * 30);
        waitUntilRowIsVisible(TOTAL_NUMBER_OF_ROWS - 1);
    }

    private void waitUntilRowIsVisible(final int row) {
        waitUntil(input -> {
            try {
                return getTable().getCell(row, 0) != null;
            } catch (NoSuchElementException e) {
                return false;
            }
        });
    }

    protected List<WebElement> getVisibleTableRows() {
        return getTable().findElements(By.cssSelector(".v-table-table tr"));
    }

}
