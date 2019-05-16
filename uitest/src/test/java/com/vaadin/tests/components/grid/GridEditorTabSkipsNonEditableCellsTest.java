package com.vaadin.tests.components.grid;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static org.junit.Assert.fail;

/**
 * Makes sure that pressing Tab when the Grid is in edit mode will make focus
 * skip cells that are not editable.
 */
@TestCategory("grid")
public class GridEditorTabSkipsNonEditableCellsTest extends MultiBrowserTest {
    /**
     * The grid with 5 columns. First, third and fifth columns are not editable.
     */
    private GridElement grid;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        grid = $(GridElement.class).first();
    }

    @Test
    public void tabSkipsOverNotEditableFieldBuffered() {
        setBuffered(true);
        openEditor(0, 1);
        pressTab();
        Assert.assertEquals("col3_0", getFocusedEditorCellContents());
    }

    @Test
    public void tabDoesNothingIfAlreadyOnLastEditableFieldBuffered() {
        setBuffered(true);
        openEditor(0, 3);
        pressTab();
        Assert.assertEquals("col3_0", getFocusedEditorCellContents());
    }

    @Test
    public void tabSkipsOverNotEditableFieldUnbuffered() {
        setBuffered(false);
        openEditor(0, 1);
        pressTab();
        Assert.assertEquals("col3_0", getFocusedEditorCellContents());
    }

    @Test
    public void tabMovesToNextRowFirstEditableFieldUnbuffered() {
        setBuffered(false);
        openEditor(0, 3);
        pressTab();
        Assert.assertEquals("col1_1", getFocusedEditorCellContents());
    }

    @Test
    public void shiftTabSkipsOverNotEditableFieldBuffered() {
        setBuffered(true);
        openEditor(0, 3);
        pressShiftTab();
        Assert.assertEquals("col1_0", getFocusedEditorCellContents());
    }

    @Test
    public void shiftTabDoesNothingIfAlreadyOnLastEditableFieldBuffered() {
        setBuffered(true);
        openEditor(0, 1);
        pressShiftTab();
        Assert.assertEquals("col1_0", getFocusedEditorCellContents());
    }

    @Test
    public void shiftTabSkipsOverNotEditableFieldUnbuffered() {
        setBuffered(false);
        openEditor(0, 3);
        pressShiftTab();
        Assert.assertEquals("col1_0", getFocusedEditorCellContents());
    }

    @Test
    public void shiftTabMovesToNextRowFirstEditableFieldUnbuffered() {
        setBuffered(false);
        openEditor(1, 1);
        pressShiftTab();
        Assert.assertEquals("col3_0", getFocusedEditorCellContents());
    }

    private void openEditor(int rowIndex, int colIndex) {
        grid.getCell(rowIndex, colIndex).doubleClick();
    }

    private void pressTab() {
        new Actions(getDriver()).sendKeys(Keys.TAB).perform();
    }

    private void pressShiftTab() {
        new Actions(getDriver()).keyDown(Keys.SHIFT).sendKeys(Keys.TAB)
                .keyUp(Keys.SHIFT).perform();
    }

    private void setBuffered(boolean buffered) {
        $(ButtonElement.class).caption(buffered ? "Set Editor Buffered Mode On"
                : "Set Editor Buffered Mode Off").first().click();
    }

    private String getFocusedEditorCellContents() {
        final GridElement.GridEditorElement editor = grid.getEditor();
        final WebElement focusedElement = getFocusedElement();
        for (int i = 0; i < 5; i++) {
            if (editor.isEditable(i)
                    && editor.getField(i).equals(focusedElement)) {
                return (editor.getField(i).wrap(TextFieldElement.class))
                        .getValue();
            }
        }
        fail("Currently focused element is not a cell editor: "
                + focusedElement);
        return null;
    }
}
