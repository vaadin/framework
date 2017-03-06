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

import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.tests.util.PortableRandom;
import com.vaadin.tests.util.TestDataGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridInitiallyHiddenColumns extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();

        grid.addColumn(Person::getFirstName).setHidden(true).setHidable(true)
                .setCaption("First Name");
        Column<Person, String> col2 = grid.addColumn(Person::getLastName)
                .setHidable(true).setCaption("Last Name");
        if (request.getParameter("allHidden") != null) {
            col2.setHidden(true);
        }
        grid.addColumn(Person::getAge, new NumberRenderer()).setHidden(true)
                .setHidable(true).setCaption("Age");

        grid.setItems(createPersons());

        addComponent(grid);
    }

    private Stream<Person> createPersons() {
        Random random = new Random(100);
        return IntStream.range(0, 100).mapToObj(index -> createPerson(random));
    }

    private Person createPerson(Random random) {
        Person person = new Person();
        person.setFirstName(TestDataGenerator.getFirstName(random));
        person.setLastName(TestDataGenerator.getLastName(random));
        person.setBirthDate(TestDataGenerator.getBirthDate(random));
        person.setAge((int) ((new Date(2014 - 1900, 1, 1).getTime()
                - person.getBirthDate().getTime()) / 1000 / 3600 / 24 / 365));

        return person;
    }

    public static String createRandomString(PortableRandom random, int len) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < len; i++) {
            b.append((char) (random.nextInt('z' - 'a') + 'a'));
        }

        return b.toString();
    }
}
