package com.vaadin.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.server.Sizeable.Unit;

public class GridLayoutExpandRatioTest {

    private GridLayout gridLayout;

    @Test
    public void testColExpandRatioIsForgotten() {
        gridLayout = new GridLayout(4, 1);
        gridLayout.setWidth(100, Unit.PERCENTAGE);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);

        addComponents(true);

        gridLayout.setColumnExpandRatio(1, 1);
        gridLayout.setColumnExpandRatio(3, 1);

        assertTrue(gridLayout.getColumnExpandRatio(0) == 0);
        assertTrue(gridLayout.getColumnExpandRatio(1) == 1);
        assertTrue(gridLayout.getColumnExpandRatio(2) == 0);
        assertTrue(gridLayout.getColumnExpandRatio(3) == 1);
        assertFalse(gridLayout.getState().explicitColRatios.contains(0));
        assertTrue(gridLayout.getState().explicitColRatios.contains(1));
        assertFalse(gridLayout.getState().explicitColRatios.contains(2));
        assertTrue(gridLayout.getState().explicitColRatios.contains(3));

        gridLayout.removeAllComponents();
        gridLayout.setColumns(3);
        addComponents(false);

        assertTrue(gridLayout.getColumnExpandRatio(0) == 0);
        assertTrue(gridLayout.getColumnExpandRatio(1) == 1);
        assertTrue(gridLayout.getColumnExpandRatio(2) == 0);
        assertTrue(gridLayout.getColumnExpandRatio(3) == 0);
        assertFalse(gridLayout.getState().explicitColRatios.contains(0));
        assertTrue(gridLayout.getState().explicitColRatios.contains(1));
        assertFalse(gridLayout.getState().explicitColRatios.contains(2));
        assertFalse(gridLayout.getState().explicitColRatios.contains(3));
    }

    @Test
    public void testRowExpandRatioIsForgotten() {
        gridLayout = new GridLayout(1, 4);
        gridLayout.setWidth(100, Unit.PERCENTAGE);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);

        addComponents(true);

        gridLayout.setRowExpandRatio(1, 1);
        gridLayout.setRowExpandRatio(3, 1);

        assertTrue(gridLayout.getRowExpandRatio(0) == 0);
        assertTrue(gridLayout.getRowExpandRatio(1) == 1);
        assertTrue(gridLayout.getRowExpandRatio(2) == 0);
        assertTrue(gridLayout.getRowExpandRatio(3) == 1);
        assertFalse(gridLayout.getState().explicitRowRatios.contains(0));
        assertTrue(gridLayout.getState().explicitRowRatios.contains(1));
        assertFalse(gridLayout.getState().explicitRowRatios.contains(2));
        assertTrue(gridLayout.getState().explicitRowRatios.contains(3));

        gridLayout.removeAllComponents();
        gridLayout.setRows(3);
        addComponents(false);

        assertTrue(gridLayout.getRowExpandRatio(0) == 0);
        assertTrue(gridLayout.getRowExpandRatio(1) == 1);
        assertTrue(gridLayout.getRowExpandRatio(2) == 0);
        assertTrue(gridLayout.getRowExpandRatio(3) == 0);
        assertFalse(gridLayout.getState().explicitRowRatios.contains(0));
        assertTrue(gridLayout.getState().explicitRowRatios.contains(1));
        assertFalse(gridLayout.getState().explicitRowRatios.contains(2));
        assertFalse(gridLayout.getState().explicitRowRatios.contains(3));
    }

    private void addComponents(boolean includeLastOne) {
        gridLayout.addComponent(label("{1}"));
        gridLayout.addComponent(label("{2}"));
        gridLayout.addComponent(label("{3}"));
        if (includeLastOne) {
            gridLayout.addComponent(label("{4}"));
        }
    }

    private Label label(String content) {
        Label label = new Label(content);
        label.setSizeUndefined();
        return label;
    }
}
