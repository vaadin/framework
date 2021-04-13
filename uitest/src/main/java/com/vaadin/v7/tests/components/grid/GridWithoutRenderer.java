package com.vaadin.v7.tests.components.grid;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.Grid;

@SuppressWarnings("deprecation")
public class GridWithoutRenderer extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();
        grid.setContainerDataSource(PersonContainer.createWithTestData());
        addComponent(grid);
        addComponent(new Button("Add error",
                e -> grid.setComponentError(new UserError("fail"))));

    }
}
