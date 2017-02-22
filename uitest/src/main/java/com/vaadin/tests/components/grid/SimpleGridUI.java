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
