package com.vaadin.v7.tests.components.grid;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.Grid;

public class InitiallyDisabledGrid extends UI {

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.setSpacing(false);
        setContent(layout);
        layout.setSizeFull();
        layout.setWidth("600px");
        layout.setHeight("600px");
        final Grid grid = createGrid();
        Button button = new Button("Enable/Disable",
                event -> grid.setEnabled(!grid.isEnabled()));

        layout.addComponent(button);
        VerticalLayout l = new VerticalLayout();
        l.setSizeFull();
        l.addComponent(grid);

        layout.addComponent(l);
        layout.setExpandRatio(l, 1.0f);
    }

    private Grid createGrid() {
        // Have some data
        Collection<Person> people = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Person person = new Person();
            person.setFirstName("First " + i);
            person.setLastName("Last " + i);
            people.add(person);
        }
        // Have a container of some type to contain the data
        BeanItemContainer<Person> container = new BeanItemContainer<>(
                Person.class, people);

        // Create a grid bound to the container
        Grid grid = new Grid(container);
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName");

        grid.setEnabled(false);

        return grid;

    }

}