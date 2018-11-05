package com.vaadin.v7.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;

@TestCategory("grid")
public class ProgrammaticEditorControlTest extends SingleBrowserTest {

    @Test
    public void multipleOpenFromServerSide() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        ButtonElement editButton = $(ButtonElement.class).caption("Edit")
                .first();
        ButtonElement cancelButton = $(ButtonElement.class).caption("Cancel")
                .first();

        editButton.click();
        assertEditorFieldContents(grid, "test");
        cancelButton.click();

        assertEditorNotPresent(grid);

        editButton.click();
        assertEditorFieldContents(grid, "test");
    }

    private void assertEditorFieldContents(GridElement grid, String text) {
        TextFieldElement editorField = wrap(TextFieldElement.class,
                grid.getEditor().getField(0));
        assertEquals(text, editorField.getValue());
    }

    private void assertEditorNotPresent(GridElement grid) {
        try {
            grid.getEditor();
            fail("Editor should not be present");
        } catch (Exception e) {

        }
    }

}
