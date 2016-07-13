package com.vaadin.tokka.tests.components;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tokka.server.communication.data.DataSource;
import com.vaadin.tokka.ui.components.grid.Grid;

public class GridSortable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Bean> grid = new Grid<Bean>();
        grid.addColumn("Never sortable", Bean::getValue).setSortable(false);
        grid.addColumn("Sortable Integer", Bean::getIntVal);
        grid.addColumn("Sortable toString()", Bean::toString);

        grid.setDataSource(DataSource.create(Bean.generateRandomBeans()));

        addComponent(grid);
    }

}
