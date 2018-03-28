package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class InitialFrozenColumns extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>(Person.class);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setColumns();
        grid.addColumn("firstName").setWidth(200);
        grid.addColumn("lastName").setWidth(200);
        grid.addColumn("email").setWidth(200);

        grid.setItems(
                new Person("First", "last", "email", 242, Sex.UNKNOWN, null));

        int frozen = 2;
        if (request.getParameter("frozen") != null) {
            frozen = Integer.parseInt(request.getParameter("frozen"));
        }
        grid.setFrozenColumnCount(frozen);

        addComponent(grid);
    }

}
