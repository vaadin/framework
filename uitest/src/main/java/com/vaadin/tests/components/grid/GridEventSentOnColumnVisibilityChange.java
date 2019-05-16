package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Grid;

public class GridEventSentOnColumnVisibilityChange
        extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        List<Person> people = Arrays.asList(
                new Person("Nicolaus Copernicus", 1543),
                new Person("Galileo Galilei", 1564),
                new Person("Johannes Kepler", 1571));

        Grid<Person> grid = new Grid<>();

        grid.setItems(people);
        grid.addColumn(Person::getName).setId("name").setCaption("Name")
                .setHidable(true);
        grid.addColumn(Person::getBirthYear).setCaption("Year of birth")
                .setHidable(true);

        grid.setSizeFull();

        grid.addColumnVisibilityChangeListener(
                event -> log("UserOriginated: " + event.isUserOriginated()));

        addComponent(grid);
    }

    private class Person {
        private final String name;
        private final int birthYear;

        public Person(String name, int birthYear) {
            this.name = name;
            this.birthYear = birthYear;
        }

        public String getName() {
            return name;
        }

        public int getBirthYear() {
            return birthYear;
        }
    }

    @Override
    public String getDescription() {
        return "Every time when the user changes the visibility of the column,"
                + " there should have only one event sent";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11419;
    }
}
