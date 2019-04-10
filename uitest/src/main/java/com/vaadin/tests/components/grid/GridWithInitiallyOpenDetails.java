package com.vaadin.tests.components.grid;

import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.ItemClick;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.ItemClickListener;

public class GridWithInitiallyOpenDetails extends SimpleGridUI {
    private List<Person> persons;

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(createGrid());
    }

    @Override
    protected List<Person> createPersons() {
        persons = super.createPersons();
        return persons;
    }

    @Override
    protected Grid<Person> createGrid() {
        Grid<Person> grid = super.createGrid();

        grid.setDetailsGenerator(
                row -> new Label("details for " + row.getFirstName()));

        for (Person person : persons) {
            grid.setDetailsVisible(person, true);
        }

        grid.addItemClickListener(new ItemClickListener<Person>() {
            @Override
            public void itemClick(ItemClick<Person> event) {
                grid.setDetailsVisible(event.getItem(),
                        !grid.isDetailsVisible(event.getItem()));
            }
        });

        return grid;
    }

    @Override
    protected String getTestDescription() {
        return "Initially open details rows should be taken into account in row positioning.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11325;
    }
}
