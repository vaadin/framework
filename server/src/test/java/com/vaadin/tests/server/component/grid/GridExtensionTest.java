package com.vaadin.tests.server.component.grid;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.AbstractGridExtension;

public class GridExtensionTest {

    public static class DummyGridExtension extends AbstractGridExtension {

        public DummyGridExtension(Grid grid) {
            super(grid);
        }
    }

    @Test
    public void testCreateExtension() {
        Grid grid = new Grid();
        DummyGridExtension dummy = new DummyGridExtension(grid);
        assertTrue("DummyGridExtension never made it to Grid",
                grid.getExtensions().contains(dummy));
    }
}
