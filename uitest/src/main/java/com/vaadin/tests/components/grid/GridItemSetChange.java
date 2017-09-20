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

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.util.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class GridItemSetChange extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();

        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getLastName);

        List<Person> persons = new ArrayList<>();
        Person person = new Person();
        person.setFirstName("Foo");
        person.setLastName("Bar");
        persons.add(person);

        ListDataProvider<Person> provider = DataProvider.ofCollection(persons);
        grid.setDataProvider(provider);

        addComponent(grid);

        addComponent(new Button("Reset", event -> {
            persons.clear();
            person.setLastName("Baz");
            persons.add(person);
            provider.refreshAll();
        }));

        addComponent(new Button("Modify", event -> {
            person.setLastName("Spam");
            provider.refreshItem(person);
        }));
    }

}
