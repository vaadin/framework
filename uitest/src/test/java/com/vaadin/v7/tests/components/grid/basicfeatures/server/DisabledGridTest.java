package com.vaadin.v7.tests.components.grid.basicfeatures.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;

public class DisabledGridTest extends GridBasicFeaturesTest {

    @Before
    public void setUp() {
        openTestURL();
        selectMenuPath("Component", "State", "Enabled");
    }

    @Test
    public void testSelection() {
        selectMenuPath("Component", "State", "Selection mode", "single");

        GridRowElement row = getGridElement().getRow(0);
        GridCellElement cell = getGridElement().getCell(0, 0);
        cell.click();
        assertFalse("disabled row should not be selected", row.isSelected());

    }

    @Test
    public void testEditorOpening() {
        selectMenuPath("Component", "Editor", "Enabled");

        GridRowElement row = getGridElement().getRow(0);
        GridCellElement cell = getGridElement().getCell(0, 0);
        cell.click();
        assertNull("Editor should not open", getEditor());

        new Actions(getDriver()).sendKeys(Keys.ENTER).perform();
        assertNull("Editor should not open", getEditor());
    }
}
