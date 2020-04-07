package com.vaadin.tests.components.grid;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class GridRecalculateColumnWidthNewItemTest extends SingleBrowserTest {

    GridElement grid;
    ButtonElement addButton;
    ButtonElement removeButton;

    @Before
    public void init() {
        openTestURL();
        grid = $(GridElement.class).first();
        addButton = $(ButtonElement.class).id("add");
        removeButton = $(ButtonElement.class).id("remove");
    }

    @Test
    public void recalculateAfterAddingAndRemovingWorks() throws IOException {
        assertEquals("CheckBox should be checked.", "checked",
                $(CheckBoxElement.class).first().getValue());

        int initialWidth = grid.getHeaderCell(0, 0).getSize().width;

        addButton.click();
        int newWidth = grid.getHeaderCell(0, 0).getSize().width;
        // ensure the column width has increased significantly
        assertThat(
                "Unexpected column width after adding a row and calling recalculate.",
                (double) newWidth, not(closeTo(initialWidth, 20)));

        removeButton.click();
        newWidth = grid.getHeaderCell(0, 0).getSize().width;
        // ensure the column width has decreased significantly (even if it might
        // not be exactly the original width)
        assertThat(
                "Unexpected column width after removing a row and calling recalculate.",
                (double) newWidth, closeTo(initialWidth, 2));
    }

    @Test
    public void addingWithoutRecalculateWorks() throws IOException {
        CheckBoxElement checkBox = $(CheckBoxElement.class).first();
        checkBox.click();
        assertEquals("CheckBox should not be checked.", "unchecked",
                checkBox.getValue());

        int initialWidth = grid.getHeaderCell(0, 0).getSize().width;

        addButton.click();
        int newWidth = grid.getHeaderCell(0, 0).getSize().width;
        // ensure the column width did not change significantly
        assertThat(
                "Unexpected column width after adding a row without calling recalculate.",
                (double) newWidth, closeTo(initialWidth, 2));
    }

    @Test
    public void removingWithoutRecalculateWorks() throws IOException {
        // add a row before unchecking
        addButton.click();

        CheckBoxElement checkBox = $(CheckBoxElement.class).first();
        checkBox.click();
        assertEquals("CheckBox should not be checked.", "unchecked",
                checkBox.getValue());

        int initialWidth = grid.getHeaderCell(0, 0).getSize().width;

        removeButton.click();
        int newWidth = grid.getHeaderCell(0, 0).getSize().width;
        // ensure the column width did not change significantly
        assertThat(
                "Unexpected column width after removing a row without calling recalculate.",
                (double) newWidth, closeTo(initialWidth, 2));
    }
}
