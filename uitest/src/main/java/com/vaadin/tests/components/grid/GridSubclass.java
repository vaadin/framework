package com.vaadin.tests.components.grid;

import com.vaadin.data.Container.Indexed;
import com.vaadin.ui.Grid;

public class GridSubclass extends Grid {

    public GridSubclass(Indexed dataSource) {
        super(dataSource);
    }
}
