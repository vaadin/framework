package com.vaadin.tests.components.treegrid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.TreeGrid;

public class TreeGridEmpty extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TreeGrid<String> grid = new TreeGrid<>();
        addComponent(grid);
    }
}
