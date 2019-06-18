package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;

@TestCategory("grid")
public class GridIndexedContainerInsertSelectTest extends SingleBrowserTest {

    @Override
    public void setup() throws Exception {

        super.setup();
        openTestURL();
        waitForElementPresent(By.className("v-grid"));
    }

    /**
     * Test asserting that issue
     * https://github.com/vaadin/framework/issues/11477 is fixed.
     */
    @Test
    public void test_insertRowAfterSelected_newRowIsSelected() {
        openTestURL();

        // Assert that first row is already selected when ui loaded
        Assert.assertTrue(
                "First row should be selected to continue with the test!",
                isRowSelected(0));

        // Add new row after the selected one
        $(ButtonElement.class).first().click();

        // Assert that the new row is added correctly
        Assert.assertEquals("Item 4",
                $(GridElement.class).first().getRow(1).getCell(0).getText());

        // Assert that the new added row is selected
        Assert.assertTrue("Newly inserted row should be selected!",
                isRowSelected(1));

        // Select row at index 2
        $(GridElement.class).first().getRow(2).click();

        // Add new row after the selected one
        $(ButtonElement.class).first().click();

        // Assert that the new row is added correctly
        Assert.assertEquals("Item 5",
                $(GridElement.class).first().getRow(3).getCell(0).getText());

        // Assert that the new added row is selected
        Assert.assertTrue("Newly inserted row should be selected!",
                isRowSelected(3));

    }

    protected boolean isRowSelected(int index) {
        return $(GridElement.class).first().getRow(index).isSelected();
    }

}
