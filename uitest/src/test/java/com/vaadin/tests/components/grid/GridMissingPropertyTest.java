package com.vaadin.tests.components.grid;

import org.junit.Test;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridMissingPropertyTest extends SingleBrowserTest {

    @Test
    public void testCellEditable() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        // Row with missing property
        grid.getCell(0, 0).doubleClick();
        GridEditorElement editor = grid.getEditor();

        assertTrue("Cell with property should be editable",
                editor.isEditable(0));
        assertFalse("Cell without property should not be editable",
                editor.isEditable(1));

        editor.cancel();

        // Row with all properties
        grid.getCell(1, 0).doubleClick();
        editor = grid.getEditor();

        assertTrue("Cell with property should be editable",
                editor.isEditable(0));
        assertTrue("Cell with property should be editable",
                editor.isEditable(1));

        editor.cancel();
    }

    @Test
    public void testEditCell() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();

        GridEditorElement editor;
        TextFieldElement editorField;

        grid.getCell(0, 0).doubleClick();
        editor = grid.getEditor();
        editorField = editor.getField(0).wrap(TextFieldElement.class);
        editorField.setValue("New Folder Name");
        editor.save();
        assertEquals("New Folder Name", grid.getCell(0, 0).getText());

        grid.getCell(1, 0).doubleClick();
        editor = grid.getEditor();
        editorField = editor.getField(1).wrap(TextFieldElement.class);
        editorField.setValue("10 MB");
        editor.save();
        assertEquals("10 MB", grid.getCell(1, 1).getText());

        grid.getCell(1, 0).doubleClick();
        editor = grid.getEditor();
        editorField = editor.getField(0).wrap(TextFieldElement.class);
        editorField.setValue("New File Name");
        editor.save();
        assertEquals("New File Name", grid.getCell(1, 0).getText());
    }
}
