package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class GridEditorEventsTest extends MultiBrowserTest {

    @Test
    public void editorEvents() throws InterruptedException {
        openTestURL();

        GridElement grid = $(GridElement.class).first();

        assertEditorEvents(0, grid);
        assertEditorEvents(1, grid);
    }

    private void assertEditorEvents(int index, GridElement grid) {
        GridEditorElement editor = updateField(index, grid, "foo");
        editor.save();

        assertEquals((index * 4 + 1) + ". editor is opened", getLogRow(1));
        assertEquals((index * 4 + 2) + ". editor is saved", getLogRow(0));

        editor = updateField(index, grid, "bar");
        editor.cancel();

        assertEquals((index * 4 + 3) + ". editor is opened", getLogRow(1));
        assertEquals((index * 4 + 4) + ". editor is canceled", getLogRow(0));
    }

    private GridEditorElement updateField(int index, GridElement grid,
            String text) {
        grid.getRow(index).doubleClick();

        GridEditorElement editor = grid.getEditor();
        WebElement focused = getFocusedElement();
        assertEquals("input", focused.getTagName());
        focused.sendKeys(text);
        return editor;
    }
}
