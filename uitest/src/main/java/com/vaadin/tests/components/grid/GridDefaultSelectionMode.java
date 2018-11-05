package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class GridDefaultSelectionMode extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Person person1 = new Person();
        person1.setFirstName("person");
        person1.setLastName("one");

        Person person2 = new Person();
        person2.setFirstName("person");
        person2.setLastName("two");

        List<Person> items = new ArrayList<>();
        items.add(person1);
        items.add(person2);

        final Grid<Person> grid = new Grid<>();
        grid.setItems(items);
        grid.addColumn(person -> person.getFirstName())
                .setCaption("First Name");
        grid.addColumn(person -> person.getLastName()).setCaption("Last Name");

        VerticalLayout v = new VerticalLayout();

        v.addComponent(new Button("Deselect on server",
                event -> grid.getSelectionModel().deselectAll()));

        v.addComponent(new Button("Select on server",
                event -> grid.getSelectionModel().select(person1)));
        v.addComponent(grid);

        addComponent(v);
    }

    public static class Person {
        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }

}
