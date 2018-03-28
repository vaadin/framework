package com.vaadin.tests.components.table;

import java.util.List;

import org.openqa.selenium.WebElement;

/**
 * Test to see if all items of the table can be selected by pressing shift and
 * selecting the first row, and then press shift then select last row (#13483)
 *
 * @author Vaadin Ltd
 */
public class SelectAllRowsShiftFirstTest extends SelectAllRowsTest {

    @Override
    protected void clickFirstRow() {
        List<WebElement> rows = getVisibleTableRows();
        shiftClickElement(rows.get(0));
    }

}
