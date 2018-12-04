package com.vaadin.v7.tests.server.component.grid.declarative;

import org.junit.Test;

import com.vaadin.ui.declarative.DesignException;
import com.vaadin.v7.ui.Grid;

public class GridStructureDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testReadEmptyGrid() {
        String design = "<vaadin7-grid />";
        testRead(design, new Grid(), false);
    }

    @Test
    public void testEmptyGrid() {
        String design = "<vaadin7-grid></vaadin7-grid>";
        Grid expected = new Grid();
        testWrite(design, expected);
        testRead(design, expected, true);
    }

    @Test(expected = DesignException.class)
    public void testMalformedGrid() {
        String design = "<vaadin7-grid><vaadin-label /></vaadin7-grid>";
        testRead(design, new Grid());
    }

    @Test(expected = DesignException.class)
    public void testGridWithNoColGroup() {
        String design = "<vaadin7-grid><table><thead><tr><th>Foo</tr></thead></table></vaadin7-grid>";
        testRead(design, new Grid());
    }
}
