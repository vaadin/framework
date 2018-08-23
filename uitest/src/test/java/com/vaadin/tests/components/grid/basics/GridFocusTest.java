package com.vaadin.tests.components.grid.basics;

import com.vaadin.testbench.annotations.RunLocally;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.parallel.Browser;
import org.junit.Assert;
import org.junit.Test;

public class GridFocusTest extends GridBasicsTest {
    private static final String ROW_FOCUSED_CLASS_POSTFIX = "-row-focused";

    @Test
    public void testFocusedRow() {
        openTestURL();
        getGridElement().focus();
        getGridElement().getRow(2).click();
        boolean rowFocusedClassExists = getGridElement().getRow(2)
                .getClassNames().stream()
                .anyMatch(s -> s.endsWith(ROW_FOCUSED_CLASS_POSTFIX));
        Assert.assertTrue("Focused row must have row-focused class.",
                rowFocusedClassExists);
    }

    @Test
    public void testUnfocusedRow() {
        openTestURL();
        getGridElement().focus();
        getGridElement().getRow(2).click();
        getGridElement().getRow(1).click();
        boolean rowFocusedClassExists = getGridElement().getRow(2)
                .getClassNames().stream()
                .anyMatch(s -> s.endsWith(ROW_FOCUSED_CLASS_POSTFIX));
        Assert.assertFalse(
                "A row without focus must not have row-focused class.",
                rowFocusedClassExists);
    }

    @Test
    public void testFocusedRowAfterGridBlur() {
        openTestURL();
        getGridElement().focus();
        getGridElement().getRow(2).click();

        // click on log label => blur
        $(LabelElement.class).first().click();

        boolean rowFocusedClassExists = getGridElement().getRow(2)
                .getClassNames().stream()
                .anyMatch(s -> s.endsWith(ROW_FOCUSED_CLASS_POSTFIX));
        Assert.assertFalse(
                "When the grid is not focused no row must have row-focused class.",
                rowFocusedClassExists);
    }

    @Test
    public void testFocusedRowAfterGridRefocus() {
        openTestURL();
        getGridElement().focus();
        getGridElement().getRow(2).click();

        // click on log label => blur
        $(LabelElement.class).first().click();

        getGridElement().focus();

        boolean rowFocusedClassExists = getGridElement().getRow(2)
                .getClassNames().stream()
                .anyMatch(s -> s.endsWith(ROW_FOCUSED_CLASS_POSTFIX));
        Assert.assertTrue(
                "Focused row must have row-focused class after the grid is focused.",
                rowFocusedClassExists);
    }
}
