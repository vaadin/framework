package com.vaadin.tests.components.grid;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridEditorElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Tests for ensuring that the furthest away visible rows don't get emptied when
 * editRow is called, and that the editor doesn't open beyond the lower border
 * of the Grid.
 *
 */
@TestCategory("grid")
public class GridEditRowTest extends MultiBrowserTest {

    private GridElement grid;
    private ButtonElement addButton;
    private ButtonElement editButton;

    @Override
    public void setup() throws Exception {
        super.setup();
        openTestURL();
        grid = $(GridElement.class).first();
        addButton = $(ButtonElement.class).caption("Add").first();
        editButton = $(ButtonElement.class).caption("Edit").first();
    }

    public void addRows(int count) {
        for (int i = 0; i < count; ++i) {
            addButton.click();
        }
    }

    public void editLastRow() {
        editButton.click();
    }

    private void assertRowContents(int rowIndex) {
        assertThat(grid.getCell(rowIndex, 0).getText(), is("name" + rowIndex));
    }

    private void assertEditorWithinGrid() {
        GridEditorElement editor = grid.getEditor();
        // allow 1px leeway
        assertThat(editor.getLocation().y + editor.getSize().height, not(
                greaterThan(grid.getLocation().y + grid.getSize().height + 1)));
    }

    @Test
    public void testEditWhenAllRowsVisible() {
        addRows(7);

        assertRowContents(0);

        editLastRow();

        assertRowContents(0);

        waitForElementVisible(By.className("v-grid-editor"));
        // wait for position corrections
        sleep(100);

        assertEditorWithinGrid();
    }

    @Test
    public void testEditWhenSomeRowsNotVisible() {
        addRows(11);

        assertRowContents(3);

        editLastRow();

        waitForElementVisible(By.className("v-grid-editor"));
        // wait for position corrections
        sleep(100);

        assertRowContents(3);
        assertEditorWithinGrid();
    }

    @Test
    public void testEditWhenSomeRowsOutsideOfCache() {
        addRows(100);

        assertRowContents(91);

        editLastRow();

        waitForElementVisible(By.className("v-grid-editor"));
        // wait for position corrections
        sleep(100);

        assertRowContents(91);
        assertEditorWithinGrid();
    }
}
