package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.VerticalLayout;

public class GridResizeAndScroll extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        addComponent(content);

        final Grid<Person> grid = new Grid<>();
        content.setHeight("500px");
        content.addComponent(grid);

        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getLastName);
        grid.addColumn(Person::getEmail);

        grid.setItems(IntStream.range(0, 50).mapToObj(this::createPerson));

        grid.setSizeFull();

        grid.setSelectionMode(SelectionMode.MULTI);

        grid.addSelectionListener(event -> {
            if (event.getAllSelectedItems().isEmpty()) {
                grid.setHeight("100%");
            } else {
                grid.setHeight("50%");
            }
        });
    }

    private Person createPerson(int index) {
        Person person = new Person();
        person.setFirstName("cell " + index + " 0");
        person.setLastName("cell " + index + " 1");
        person.setEmail("cell " + index + " 2");
        return person;
    }
}
