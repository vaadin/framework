package com.vaadin.tests.components.grid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.shared.ui.grid.DropMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.components.grid.GridDropTarget;

public class GridDropTargetTest {

    private Grid<String> grid;
    private GridDropTarget<String> target;

    @Before
    public void setup() {
        grid = new Grid<>();
        grid.addColumn(s -> s).setId("1");
        grid.addColumn(s -> s).setId("2");
        target = new GridDropTarget<>(grid, DropMode.BETWEEN);
    }

    @Test
    public void dropAllowedOnSortedGridRows_defaultValue_isTrue() {
        Assert.assertTrue("Default drop allowed should be backwards compatible",
                target.isDropAllowedOnRowsWhenSorted());
    }

    @Test
    public void dropAllowedOnSortedGridRows_notAllowed_changesDropModeWhenSorted() {
        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(false);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        grid.sort("1");

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());

        grid.sort("2");

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());

        grid.clearSortOrder();

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        grid.clearSortOrder();

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        grid.sort("2");

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_sortedGridIsDisallowed_modeChangesToOnGrid() {
        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        grid.sort("1");

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(false);

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(true);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_notAllowedBackToAllowed_changesBackToUserDefinedMode() {
        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(false);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        grid.sort("1");

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(true);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        grid.clearSortOrder();

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_swappingAllowedDropOnSortedOffAndOn() {
        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(false);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(false);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(true);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(true);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());
    }

    @Test
    public void dropAllowedOnSortedGridRows_changingDropModeWhileSorted_replacesPreviouslyCachedButDoesntOverride() {
        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(false);

        Assert.assertEquals(DropMode.BETWEEN, target.getDropMode());

        grid.sort("1");

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());

        target.setDropMode(DropMode.ON_TOP);

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());
        Assert.assertFalse("Changing drop mode should not have any effect here",
                target.isDropAllowedOnRowsWhenSorted());

        grid.clearSortOrder();

        Assert.assertEquals(DropMode.ON_TOP, target.getDropMode());

        grid.sort("1");

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());

        target.setDropMode(DropMode.ON_TOP_OR_BETWEEN);

        Assert.assertEquals(DropMode.ON_GRID, target.getDropMode());

        target.setDropAllowedOnRowsWhenSorted(true);

        Assert.assertEquals(DropMode.ON_TOP_OR_BETWEEN, target.getDropMode());
    }
}
