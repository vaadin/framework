package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;

public class GridScrolledToBottom extends SimpleGridUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = createGrid();
        grid.setSizeFull();

        VerticalSplitPanel splitPanel = new VerticalSplitPanel(grid,
                new Label("Foo"));
        splitPanel.setHeight("200px");
        splitPanel.setSplitPosition(100);
        getLayout().addComponent(splitPanel);
    }

    @Override
    protected List<Person> createPersons() {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            Person person = new Person();
            person.setFirstName("Person " + i);
            person.setAge(i);
            persons.add(person);
        }
        return persons;
    }

    @Override
    protected String getTestDescription() {
        return "Resizing a Grid when it's scrolled to bottom shouldn't cause indexing to jump.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11044;
    }
}
