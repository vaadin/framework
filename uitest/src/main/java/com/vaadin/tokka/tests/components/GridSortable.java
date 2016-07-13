package com.vaadin.tokka.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.ui.components.grid.Grid;

public class GridSortable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Bean> grid = new Grid<Bean>();
        grid.addColumn("Sortable String", Bean::getValue);
        grid.addColumn("Sortable Integer", Bean::getIntVal);
        grid.addColumn("Not sortable toString()", Bean::toString).setSortable(
                false);

        grid.setDataSource(DataSource.create(new Bean("Foo", 0), new Bean(
                "Bar", 1), new Bean("Bar", 0)));

        addComponent(grid);
    }
}
