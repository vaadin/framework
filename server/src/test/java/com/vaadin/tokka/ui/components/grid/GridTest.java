package com.vaadin.tokka.ui.components.grid;

import org.junit.Test;

public class GridTest {

    @Test
    public void testAddColumnWithoutAttach() {
        Grid<String> grid = new Grid<>();
        grid.addColumn("Length", String::length);
    }
}
