package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.v7.ui.Grid;

public class NullHeaders extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid();
        grid.addColumn("country", String.class);
        grid.addColumn("foo", String.class);
        grid.addColumn("bar", Integer.class);

        grid.getColumn("country").setHeaderCaption(null);
        grid.getColumn("foo").setHeaderCaption("");
        grid.getColumn("bar").setHeaderCaption(null);
        grid.addRow("Finland", "foo", 1);
        grid.addRow("Swaziland", "bar", 2);
        grid.addRow("Japan", "baz", 3);
        addComponent(grid);
    }

}
