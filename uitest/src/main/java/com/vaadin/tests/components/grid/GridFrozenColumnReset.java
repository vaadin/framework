package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridFrozenColumnReset extends SimpleGridUI {

    private Grid<Person> grid;

    @Override
    protected void setup(VaadinRequest request) {
        grid = new Grid<Person>();
        grid.setSizeFull();
        init();
        getLayout().addComponent(grid);

        Button button = new Button("change frozen count");
        button.addClickListener(event -> {
            reInit();
        });
        getLayout().addComponent(button);
    }

    @Override
    protected List<Person> createPersons() {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            Person person = new Person();
            person.setFirstName("First " + i);
            person.setLastName("Last" + i);
            person.setAge(i);
            persons.add(person);
        }
        return persons;
    }

    protected void init() {
        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getLastName);
        grid.addColumn(Person::getAge, new NumberRenderer());

        grid.setItems(createPersons());
        grid.setFrozenColumnCount(2);
    }

    protected void reInit() {
        grid.removeAllColumns();
        init();
    }

}
