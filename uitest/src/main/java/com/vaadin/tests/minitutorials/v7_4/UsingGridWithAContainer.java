package com.vaadin.tests.minitutorials.v7_4;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.Grid;

public class UsingGridWithAContainer extends UI {
    @Override
    protected void init(VaadinRequest request) {
        Grid grid = new Grid();
        grid.setContainerDataSource(GridExampleHelper.createContainer());

        grid.getColumn("name").setHeaderCaption("Bean name");
        grid.removeColumn("count");
        grid.setColumnOrder("name", "amount");

        setContent(grid);
    }
}
