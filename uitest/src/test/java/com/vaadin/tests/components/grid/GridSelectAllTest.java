package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.tests.components.grid.basics.GridBasicsTest;

public class GridSelectAllTest extends GridBasicsTest {

    @Test
    public void testSelectAllCheckbox() {
        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        assertTrue("No checkbox", header.isElementPresent(By.tagName("input")));
        header.findElement(By.tagName("input")).click();

        for (int i = 0; i < GridBasicsTest.ROWS; i += 100) {
            assertTrue("Row " + i + " was not selected.",
                    getGridElement().getRow(i).isSelected());
        }

        header.findElement(By.tagName("input")).click();
        assertFalse("Row 100 was still selected",
                getGridElement().getRow(100).isSelected());
    }

    @Test
    public void testSelectAllAndSort() {
        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(0, 0);

        header.findElement(By.tagName("input")).click();

        getGridElement().getHeaderCell(0, 1).click();

        WebElement selectionBox = getGridElement().getCell(4, 0)
                .findElement(By.tagName("input"));
        selectionBox.click();
        selectionBox.click();

        assertFalse("Exception occured on row reselection.", logContainsText(
                "Exception occured, java.lang.IllegalStateException: No item id for key 101 found."));
    }

    @Test
    public void testSelectAllCheckboxWhenChangingModels() {
        GridCellElement header;
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for Single Selection Model",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelMulti();
        header = getGridElement().getHeaderCell(0, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelSingle();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for Single Selection Model",
                header.isElementPresent(By.tagName("input")));

        // Single selection model shouldn't have selection column to begin with
        assertFalse(
                "Selection columnn shouldn't have been in grid for Single Selection Model",
                getGridElement().getCell(0, 1)
                        .isElementPresent(By.tagName("input")));

        setSelectionModelSingle();
        header = getGridElement().getHeaderCell(0, 0);
        assertFalse(
                "Check box shouldn't have been in header for None Selection Model",
                header.isElementPresent(By.tagName("input")));
    }

    @Test
    public void testSelectAllCheckboxWithHeaderOperations() {
        setSelectionModelMulti();
        selectMenuPath("Component", "Header", "Prepend header row");
        assertEquals(2, getGridElement().getHeaderCount());
        selectMenuPath("Component", "Header", "Append header row");
        assertEquals(3, getGridElement().getHeaderCount());

        GridCellElement header = getGridElement().getHeaderCell(1, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));
    }

    @Test
    public void testSelectAllCheckboxAfterPrependHeaderOperations() {
        selectMenuPath("Component", "Header", "Prepend header row");
        assertEquals(2, getGridElement().getHeaderCount());

        setSelectionModelMulti();
        GridCellElement header = getGridElement().getHeaderCell(1, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));

        setSelectionModelSingle();
        header = getGridElement().getHeaderCell(1, 0);
        assertFalse(
                "Check box shouldn't have been in header for Single Selection Model",
                header.isElementPresent(By.tagName("input")));

        selectMenuPath("Component", "Header", "Append header row");
        assertEquals(3, getGridElement().getHeaderCount());

        setSelectionModelMulti();
        header = getGridElement().getHeaderCell(1, 0);
        assertTrue("Multi Selection Model should have select all checkbox",
                header.isElementPresent(By.tagName("input")));
    }

    @Test
    public void testSelectAllCheckbox_selectedAllFromClient_afterDeselectingOnClientSide_notSelected() {
        setSelectionModelMulti();

        verifyAllSelected(false);

        getSelectAllCheckbox().click();

        verifyAllSelected(true);

        getGridElement().getCell(5, 0).click();

        verifyAllSelected(false);

        getGridElement().getCell(5, 0).click();

        verifyAllSelected(false); // EXPECTED since multiselection model can't
                                  // verify that all have been selected
    }

    @Test
    public void testSelectAllCheckbox_selectedAllFromClient_afterDeselectingOnServerSide_notSelected() {
        setSelectionModelMulti();

        verifyAllSelected(false);

        getSelectAllCheckbox().click();

        verifyAllSelected(true);

        toggleFirstRowSelection();

        verifyAllSelected(false);

        toggleFirstRowSelection();

        verifyAllSelected(false); // EXPECTED since multiselection model can't
                                  // verify that all have been selected
    }

    @Test
    public void testSelectAllCheckbox_selectedAllFromServer_afterDeselectingOnClientSide_notSelected() {
        selectAll(); // triggers selection model change

        verifyAllSelected(true);

        getGridElement().getCell(5, 0).click();

        verifyAllSelected(false);

        getGridElement().getCell(5, 0).click();

        verifyAllSelected(false); // EXPECTED since multiselection model can't
                                  // verify that all have been selected
    }

    @Test
    public void testSelectAllCheckbox_selectedAllFromServer_afterDeselectingOnServerSide_notSelected() {
        selectAll(); // triggers selection model change

        verifyAllSelected(true);

        toggleFirstRowSelection();

        verifyAllSelected(false);

        toggleFirstRowSelection();

        verifyAllSelected(false); // EXPECTED since multiselection model can't
                                  // verify that all have been selected
    }

    @Test
    public void testSelectAllCheckbox_triggerVisibility() {
        verifySelectAllNotVisible();

        setSelectionModelMulti();

        verifySelectAllVisible();

        setSelectAllCheckBoxHidden();

        verifySelectAllNotVisible();

        setSelectAllCheckBoxDefault();

        verifySelectAllVisible(); // visible because in memory data provider

        setSelectAllCheckBoxHidden();

        verifySelectAllNotVisible();

        setSelectAllCheckBoxVisible();

        verifySelectAllVisible();
    }

    @Test
    public void testSelectAllCheckboxNotVisible_selectAllFromServer_staysHidden() {
        setSelectionModelMulti();

        verifySelectAllVisible();

        setSelectAllCheckBoxHidden();

        verifySelectAllNotVisible();

        selectAll();

        verifySelectAllNotVisible();
    }

    @Test
    public void testSelectAll_immediatelyWhenSettingSelectionModel() {
        verifySelectAllNotVisible();

        selectAll(); // changes selection model too

        verifyAllSelected(true);
    }

    @Test
    public void testSelectAllCheckBoxHidden_immediatelyWhenChaningModel() {
        verifySelectAllNotVisible();

        setSelectAllCheckBoxHidden(); // changes selection model

        verifySelectAllNotVisible();
    }

    private void verifyAllSelected(boolean selected) {
        verifySelectAllVisible();
        assertEquals("Select all checkbox selection state wrong", selected,
                getSelectAllCheckbox().isSelected());
    }

    private void verifySelectAllVisible() {
        assertTrue("Select all checkbox should be displayed",
                getSelectAllCheckbox().isDisplayed());
    }

    private void verifySelectAllNotVisible() {
        assertEquals("Select all checkbox should not be displayed", 0,
                getGridElement().getHeaderCell(0, 0)
                        .findElements(By.tagName("input")).size());
    }

}
