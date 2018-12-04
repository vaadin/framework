package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Grid;

@Theme("valo")
public class GridEditorFrozenColumnsUI extends GridEditorUI {

    @Override
    protected Grid<Person> createGrid() {
        Grid<Person> grid = super.createGrid();

        grid.setFrozenColumnCount(2);

        grid.setWidth("600px");
        grid.setHeight("100%");

        return grid;
    }

    @Override
    protected Integer getTicketNumber() {
        return 16727;
    }

    @Override
    protected String getTestDescription() {
        return "Frozen columns should also freeze cells in editor.";
    }
}
