/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.grid;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.Assert;
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
        Assert.assertEquals("first last", col.getValueProvider().apply(person));
    }

    @Test
    public void getBeanColumnValueProvider() {
        Grid<Person> grid = new Grid<>(Person.class);
        Column<Person, String> col = (Column<Person, String>) grid
                .getColumn("email");
        Person person = new Person("first", "last", "eeemaaail", 123,
                Sex.UNKNOWN, null);
        Assert.assertEquals("eeemaaail", col.getValueProvider().apply(person));

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
                value -> value.toUpperCase(Locale.ENGLISH).equals(value));

        List<Person> queryPersons = persons.fetch(new Query<>())
                .collect(Collectors.toList());
        Assert.assertEquals(1, queryPersons.size());
        Assert.assertSame(upperCasePerson, queryPersons.get(0));
    }
}
