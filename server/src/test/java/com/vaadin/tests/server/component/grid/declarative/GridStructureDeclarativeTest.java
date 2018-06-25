package com.vaadin.tests.server.component.grid.declarative;

import org.junit.Test;

import com.vaadin.ui.Grid;
import com.vaadin.ui.declarative.DesignException;

public class GridStructureDeclarativeTest extends GridDeclarativeTestBase {

    @Test
    public void testReadEmptyGrid() {
        String design = "<vaadin-grid />";
        testRead(design, new Grid(), false);
    }

    @Test
    public void testEmptyGrid() {
        String design = "<vaadin-grid></vaadin-grid>";
        Grid expected = new Grid();
        testWrite(design, expected);
        testRead(design, expected, true);
    }

    @Test(expected = DesignException.class)
    public void testMalformedGrid() {
        String design = "<vaadin-grid><vaadin-label /></vaadin-grid>";
        testRead(design, new Grid());
    }

    @Test(expected = DesignException.class)
    public void testGridWithNoColGroup() {
        String design = "<vaadin-grid><table><thead><tr><th>Foo</tr></thead></table></vaadin-grid>";
        testRead(design, new Grid());
    }
}
