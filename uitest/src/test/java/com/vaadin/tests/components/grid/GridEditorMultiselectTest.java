package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridEditorMultiselectTest extends MultiBrowserTest {

    @Test
    public void testSelectCheckboxesDisabled() {
        openTestURL();
        GridElement grid = openEditor();
        assertCheckboxesEnabled(grid, false);
    }

    @Test
    public void testSelectCheckboxesEnabledBackOnSave() {
        openTestURL();
        GridElement grid = openEditor();
        waitForElementPresent(By.className("v-grid-editor-save"));
        sleep(100); // wait for repositioning
        findElement(By.className("v-grid-editor-save")).click();
        waitForElementNotPresent(By.className("v-grid-editor-cells"));
        assertCheckboxesEnabled(grid, true);
    }

    @Test
    public void testSelectCheckboxesEnabledBackOnCancel() {
        openTestURL();
        GridElement grid = openEditor();
        waitForElementPresent(By.className("v-grid-editor-cancel"));
        sleep(100); // wait for repositioning
        findElement(By.className("v-grid-editor-cancel")).click();
        waitForElementNotPresent(By.className("v-grid-editor-cells"));
        assertCheckboxesEnabled(grid, true);
    }

    private GridElement openEditor() {
        GridElement grid = $(GridElement.class).first();
        grid.getRow(0).doubleClick();
        waitForElementPresent(By.className("v-grid-editor-cells"));
        assertTrue("Grid editor should be displayed.",
                grid.getEditor().isDisplayed());
        return grid;
    }

    private void assertCheckboxesEnabled(GridElement grid, boolean isEnabled) {
        List<WebElement> checkboxes = grid
                .findElements(By.xpath("//input[@type='checkbox']"));
        for (WebElement checkbox : checkboxes) {
            assertEquals(
                    "Select checkboxes should be "
                            + (isEnabled ? "enabled" : "disabled"),
                    isEnabled, checkbox.isEnabled());
        }
    }
}
