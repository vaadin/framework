package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridDetailsAndUndefinedHeight extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ArrayList<Person> itemsCollection = new ArrayList<>();

        final Grid<Person> grid = new Grid<Person>() {
            @Override
            public void setItems(Collection<Person> items) {
                itemsCollection.clear();
                itemsCollection.addAll(items);
                super.setItems(items);
            }
        };
        addComponent(grid);

        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getLastName);
        grid.addColumn(Person::getEmail);

        grid.setItems(IntStream.range(0, 5).mapToObj(this::createPerson));

        grid.setWidthFull();
        grid.setHeightMode(HeightMode.UNDEFINED);

        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.addSelectionListener(event -> {
            itemsCollection
                    .forEach(item -> grid.setDetailsVisible(item, false));
            final Set<Person> selections = event.getAllSelectedItems();
            if (!selections.isEmpty()) {
                Person selection = selections.iterator().next();
                grid.setDetailsVisible(selection, true);
                grid.scrollTo(itemsCollection.indexOf(selection));
            }
        });
        grid.setDetailsGenerator(person -> new VerticalLayout(
                new Label("Details " + itemsCollection.indexOf(person))));
    }

    private Person createPerson(int index) {
        Person person = new Person();
        person.setFirstName("cell " + index + " 0");
        person.setLastName("cell " + index + " 1");
        person.setEmail("cell " + index + " 2");
        return person;
    }

    @Override
    protected String getTestDescription() {
        return "Second selection should successfully close "
                + "the details row from the first selection.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11856;
    }
}
