package com.vaadin.tests.server.component.grid;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.shared.ui.grid.GridState;
import com.vaadin.ui.Grid;

/**
 * Tests for Grid State.
 *
 */
public class GridStateTest {

    @Test
    public void getPrimaryStyleName_gridHasCustomPrimaryStyleName() {
        Grid grid = new Grid();
        GridState state = new GridState();
        Assert.assertEquals("Unexpected primary style name",
                state.primaryStyleName, grid.getPrimaryStyleName());
    }

    @Test
    public void gridStateHasCustomPrimaryStyleName() {
        GridState state = new GridState();
        Assert.assertEquals("Unexpected primary style name", "v-grid",
                state.primaryStyleName);
    }
}
