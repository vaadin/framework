package com.vaadin.tests.server.component.gridlayout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;

public class GridLayoutTest {
    Component[] children = new Component[] { new Label("A"), new Label("B"),
            new Label("C"), new Label("D") };

    @Test
    public void testConstructorWithComponents() {
        GridLayout grid = new GridLayout(2, 2, children);
        assertContentPositions(grid);
        assertOrder(grid, new int[] { 0, 1, 2, 3 });

        grid = new GridLayout(1, 1, children);
        assertContentPositions(grid);
        assertOrder(grid, new int[] { 0, 1, 2, 3 });
    }

    @Test
    public void testAddComponents() {
        GridLayout grid = new GridLayout(2, 2);
        grid.addComponents(children);
        assertContentPositions(grid);
        assertOrder(grid, new int[] { 0, 1, 2, 3 });

        Label extra = new Label("Extra");
        Label extra2 = new Label("Extra2");
        grid.addComponents(extra, extra2);
        assertSame(grid.getComponent(0, 2), extra);
        assertSame(grid.getComponent(1, 2), extra2);

        grid.removeAllComponents();
        grid.addComponents(extra, extra2);
        assertSame(grid.getComponent(0, 0), extra);
        assertSame(grid.getComponent(1, 0), extra2);

        grid.addComponents(children);
        assertOrder(grid, new int[] { -1, -1, 0, 1, 2, 3 });

        grid.removeComponent(extra);
        grid.removeComponent(extra2);
        assertOrder(grid, new int[] { 0, 1, 2, 3 });

        grid.addComponents(extra2, extra);
        assertSame(grid.getComponent(0, 3), extra2);
        assertSame(grid.getComponent(1, 3), extra);
        assertOrder(grid, new int[] { 0, 1, 2, 3, -1, -1 });

        grid.removeComponent(extra2);
        grid.removeComponent(extra);
        grid.setCursorX(0);
        grid.setCursorY(0);
        grid.addComponents(extra, extra2);
        assertSame(grid.getComponent(0, 0), extra);
        assertSame(grid.getComponent(1, 0), extra2);
        assertOrder(grid, new int[] { -1, -1, 0, 1, 2, 3 });

        grid = new GridLayout();
        grid.addComponents(children);
        assertContentPositions(grid);
        assertOrder(grid, new int[] { 0, 1, 2, 3 });
    }

    @Test
    public void removeRowsExpandRatiosPreserved() {
        GridLayout gl = new GridLayout(3, 3);
        gl.setRowExpandRatio(0, 0);
        gl.setRowExpandRatio(1, 1);
        gl.setRowExpandRatio(2, 2);

        gl.setRows(2);
        assertEquals(0, gl.getRowExpandRatio(0), 0);
        assertEquals(1, gl.getRowExpandRatio(1), 0);
    }

    @Test
    public void removeColsExpandRatiosPreserved() {
        GridLayout gl = new GridLayout(3, 3);
        gl.setColumnExpandRatio(0, 0);
        gl.setColumnExpandRatio(1, 1);
        gl.setColumnExpandRatio(2, 2);

        gl.setColumns(2);
        assertEquals(0, gl.getColumnExpandRatio(0), 0);
        assertEquals(1, gl.getColumnExpandRatio(1), 0);
    }

    private void assertContentPositions(GridLayout grid) {
        assertEquals(grid.getComponentCount(), children.length);
        int c = 0;
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getColumns(); j++) {
                assertSame(grid.getComponent(j, i), children[c]);
                c++;
            }
        }
    }

    /**
     * Asserts that layout has the components in children in the order specified
     * by indices.
     */
    private void assertOrder(Layout layout, int[] indices) {
        Iterator<?> i = layout.iterator();
        try {
            for (int index : indices) {
                if (index != -1) {
                    assertSame(children[index], i.next());
                } else {
                    i.next();
                }
            }
            assertFalse("Too many components in layout", i.hasNext());
        } catch (NoSuchElementException e) {
            fail("Too few components in layout");
        }
    }
}
