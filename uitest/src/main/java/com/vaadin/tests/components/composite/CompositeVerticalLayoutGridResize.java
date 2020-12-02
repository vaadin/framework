package com.vaadin.tests.components.composite;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;

public class CompositeVerticalLayoutGridResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new CompositeGrid());

        getLayout().setSizeFull();
        getLayout().getParent().setSizeFull();
    }

    public class CompositeGrid extends Composite {
        public CompositeGrid() {
            VerticalLayout root = new VerticalLayout();
            root.setId("root");
            root.setMargin(false);
            root.addComponentsAndExpand(buildGrid());

            setCompositionRoot(root);
            setSizeFull();
        }

        private Component buildGrid() {
            List<Person> persons = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                persons.add(new Person("Firstname" + i, "Lastname" + i));
            }

            Grid<Person> grid = new Grid<Person>(Person.class);
            grid.setItems(persons);
            grid.setSizeFull();
            return grid;
        }
    }

    public class Person {
        private String firstName, lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }
    }

    @Override
    protected String getTestDescription() {
        return "Composite contents should resize without a delay when the"
                + " browser is resized, not only when interacted with.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12153;
    }
}
