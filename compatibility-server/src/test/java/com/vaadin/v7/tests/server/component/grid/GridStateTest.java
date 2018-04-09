package com.vaadin.v7.tests.server.component.grid;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.v7.shared.ui.grid.GridState;
import com.vaadin.v7.ui.Grid;

/**
 * Tests for Grid State.
 *
 */
public class GridStateTest {

    @Test
    public void getPrimaryStyleName_gridHasCustomPrimaryStyleName() {
        Grid grid = new Grid();
        GridState state = new GridState();
        assertEquals("Unexpected primary style name", state.primaryStyleName,
                grid.getPrimaryStyleName());
    }

    @Test
    public void gridStateHasCustomPrimaryStyleName() {
        GridState state = new GridState();
        assertEquals("Unexpected primary style name", "v-grid",
                state.primaryStyleName);
    }
}
