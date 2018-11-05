package com.vaadin.tests.components.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Test;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.data.bean.Sex;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;

public class GridValueProvider {

    @Test
    public void getExplicitValueProvider() {
        Grid<Person> grid = new Grid<>();
        Column<Person, String> col = grid.addColumn(
                person -> person.getFirstName() + " " + person.getLastName());
        Person person = new Person("first", "last", "email", 123, Sex.UNKNOWN,
                null);
        assertEquals("first last", col.getValueProvider().apply(person));
    }

    @Test
    public void getBeanColumnValueProvider() {
        Grid<Person> grid = new Grid<>(Person.class);
        Column<Person, String> col = (Column<Person, String>) grid
                .getColumn("email");
        Person person = new Person("first", "last", "eeemaaail", 123,
                Sex.UNKNOWN, null);
        assertEquals("eeemaaail", col.getValueProvider().apply(person));

    }

    @Test
    public void reuseValueProviderForFilter() {
        Grid<Person> grid = new Grid<>(Person.class);
        Column<Person, String> col = (Column<Person, String>) grid
                .getColumn("email");

        Person lowerCasePerson = new Person("first", "last", "email", 123,
                Sex.UNKNOWN, null);
        Person upperCasePerson = new Person("FIRST", "LAST", "EMAIL", 123,
                Sex.UNKNOWN, null);
        ListDataProvider<Person> persons = DataProvider.ofItems(lowerCasePerson,
                upperCasePerson);

        persons.addFilter(col.getValueProvider(),
                value -> value.toUpperCase(Locale.ROOT).equals(value));

        List<Person> queryPersons = persons.fetch(new Query<>())
                .collect(Collectors.toList());
        assertEquals(1, queryPersons.size());
        assertSame(upperCasePerson, queryPersons.get(0));
    }
}
