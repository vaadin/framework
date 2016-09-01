package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Grid;

public class GridSelectionTest {

    Grid<String> grid;

    @Before
    public void setUp() {
        grid = new Grid<>();
        grid.setItems("Foo", "Bar");
    }

    @Test
    public void testGridWithSingleSelection() {
        Assert.assertFalse(grid.isSelected("Foo"));
        grid.select("Foo");
        Assert.assertTrue(grid.isSelected("Foo"));
        Assert.assertEquals(1, grid.getSelectedItems().size());
        Assert.assertEquals("Foo", grid.getSelectedItems().iterator().next());
        grid.select("Bar");
        Assert.assertFalse(grid.isSelected("Foo"));
        Assert.assertTrue(grid.isSelected("Bar"));
        grid.deselect("Bar");
        Assert.assertFalse(grid.isSelected("Bar"));
        Assert.assertEquals(0, grid.getSelectedItems().size());
    }

}
