package com.vaadin.tests.fieldgroup;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@TestCategory("grid")
public class BasicCrudGridEditorRowTest extends MultiBrowserTest {
    private GridElement grid;

    @Before
    public void openTest() {
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void lookAndFeel() throws Exception {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        waitUntilLoadingIndicatorNotVisible();
        assertEquals("May 16, 1992", ritaBirthdate.getText());

        // Grid Editor should not present yet
        waitForElementNotPresent(By.className("v-grid-editor"));
        // Open editor row
        new Actions(getDriver()).doubleClick(ritaBirthdate).perform();
        sleep(200);
        // Compound class name is not allowed to use here,
        // check the v-grid-editor class only in this case.
        waitForElementPresent(By.className("v-grid-editor"));
    }

    @Test
    public void editorRowOneInvalidValue() throws Exception {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        // Open editor row
        new Actions(getDriver()).doubleClick(ritaBirthdate).perform();

        GridEditorElement editor = grid.getEditor();
        DateFieldElement dateField = editor.$(DateFieldElement.class).first();
        WebElement input = dateField.findElement(By.xpath("input"));
        // input.click();
        input.sendKeys("Invalid", Keys.TAB);
        editor.save();

        assertTrue("Editor wasn't displayed.", editor.isDisplayed());
        assertTrue("DateField wasn't displayed.", dateField.isDisplayed());

        assertTrue("DateField didn't have 'v-invalid' css class.",
                hasCssClass(dateField, "v-datefield-error"));
    }

    @Test
    public void testCheckboxInEditorWorks() {
        GridCellElement ritaBirthdate = grid.getCell(2, 3);
        // Open editor row
        new Actions(getDriver()).doubleClick(ritaBirthdate).perform();

        // Get CheckBox
        GridEditorElement editor = grid.getEditor();
        CheckBoxElement cb = editor.getField(5).wrap(CheckBoxElement.class);

        // Check values
        String value = cb.getValue();
        cb.click();
        assertNotEquals("Checkbox value did not change", value, cb.getValue());
    }

    @Test
    public void testNoTopStyleSetOnEditorOpenWithFooterOnTop() {
        GridCellElement cell = grid.getCell(2, 3);
        // Open editor row
        new Actions(getDriver()).doubleClick(cell).perform();

        // Close editor
        new Actions(getDriver()).sendKeys(Keys.ESCAPE).perform();

        cell = grid.getCell(14, 3);

        // Open editor row
        new Actions(getDriver()).doubleClick(cell).perform();

        String attribute = grid.getEditor().getAttribute("style")
                .toLowerCase(Locale.ROOT);
        assertFalse("Style should not contain top.",
                attribute.contains("top:"));
    }

}
