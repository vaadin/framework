package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class SimpleGridUI extends AbstractTestUIWithLog {

    protected Grid<Person> createGrid() {
        Grid<Person> grid = new Grid<>();

        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getAge, new NumberRenderer());

        grid.setItems(createPersons());
        return grid;
    }

    protected List<Person> createPersons() {
        List<Person> persons = new ArrayList<>();
        Person person = new Person();
        person.setFirstName("Nicolaus Copernicus");
        person.setAge(1543);
        persons.add(person);

        person = new Person();
        person.setFirstName("Galileo Galilei");
        person.setAge(1564);
        persons.add(person);

        person = new Person();
        person.setFirstName("Johannes Kepler");
        person.setAge(1571);
        persons.add(person);

        return persons;
    }

}
