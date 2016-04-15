package com.vaadin.tests.components.grid;

import java.util.List;

import org.junit.Assert;
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
        grid.getEditor().save();
        assertCheckboxesEnabled(grid, true);
    }

    @Test
    public void testSelectCheckboxesEnabledBackOnCancel() {
        openTestURL();
        GridElement grid = openEditor();
        grid.getEditor().cancel();
        assertCheckboxesEnabled(grid, true);
    }

    private GridElement openEditor() {
        GridElement grid = $(GridElement.class).first();
        grid.getRow(0).doubleClick();
        Assert.assertTrue("Grid editor should be displayed.", grid.getEditor()
                .isDisplayed());
        return grid;
    }

    private void assertCheckboxesEnabled(GridElement grid, boolean isEnabled) {
        List<WebElement> checkboxes = grid.findElements(By
                .xpath("//input[@type='checkbox']"));
        for (WebElement checkbox : checkboxes) {
            Assert.assertEquals("Select checkboxes should be "
                    + (isEnabled ? "enabled" : "disabled"), isEnabled,
                    checkbox.isEnabled());
        }
    }
}
