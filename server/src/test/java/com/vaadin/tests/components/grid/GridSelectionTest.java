package com.vaadin.tests.components.grid;

import java.util.Optional;

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
        Assert.assertFalse(grid.getSelectionModel().isSelected("Foo"));
        grid.getSelectionModel().select("Foo");
        Assert.assertTrue(grid.getSelectionModel().isSelected("Foo"));
        Assert.assertEquals(Optional.of("Foo"), grid.getSelectedItem());
        grid.getSelectionModel().select("Bar");
        Assert.assertFalse(grid.getSelectionModel().isSelected("Foo"));
        Assert.assertTrue(grid.getSelectionModel().isSelected("Bar"));
        grid.getSelectionModel().deselect("Bar");
        Assert.assertFalse(grid.getSelectionModel().isSelected("Bar"));
        Assert.assertFalse(
                grid.getSelectionModel().getSelectedItem().isPresent());
    }

}
