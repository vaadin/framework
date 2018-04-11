package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.Keys;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

@TestCategory("grid")
public class GridEditorCustomFieldTest extends MultiBrowserTest {

    @Test
    public void testCustomFieldWorksInEditorRow() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertEquals("Stockholm", grid.getCell(0, 2).getText());
        grid.getCell(0, 1).doubleClick();
        GridEditorElement editor = grid.getEditor();
        TestBenchElement customField = editor.getField(2);

        ComboBoxElement comboBox = customField.$(ComboBoxElement.class).first();
        comboBox.selectByText("Oslo");
        editor.save();
        assertEquals("Oslo", grid.getCell(0, 2).getText());

    }

    @Test
    public void tabReachesCustomField() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        grid.getCell(0, 1).doubleClick();
        GridEditorElement editor = grid.getEditor();
        editor.getField(0).sendKeys(Keys.TAB, Keys.TAB);

        ComboBoxElement comboBoxInCustomField = editor.getField(2)
                .$(ComboBoxElement.class).first();
        assertElementsEquals(comboBoxInCustomField.getInputField(),
                getActiveElement());
    }
}
