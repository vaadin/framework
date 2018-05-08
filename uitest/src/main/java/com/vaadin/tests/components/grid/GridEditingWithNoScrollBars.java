package com.vaadin.tests.components.grid;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;

public class GridEditingWithNoScrollBars extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName);
        Column<Person, String> column = grid.addColumn(Person::getLastName);

        grid.setItems(IntStream.range(0, 10).mapToObj(this::createPerson));

        ComboBox<String> stCombo = new ComboBox<>();
        stCombo.setItems(Arrays.asList("1", "2", "3"));
        stCombo.setEmptySelectionAllowed(false);
        stCombo.setSizeFull();

        column.setEditorComponent(stCombo, Person::setLastName);

        grid.setSelectionMode(SelectionMode.SINGLE);
        grid.getEditor().setEnabled(true);
        grid.setSizeFull();

        addComponent(grid);
    }

    private Person createPerson(int i) {
        Person person = new Person();
        person.setFirstName("foo");
        person.setLastName(String.valueOf(i % 3 + 1));
        return person;
    }

}
