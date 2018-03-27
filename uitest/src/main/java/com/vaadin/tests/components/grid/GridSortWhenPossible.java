/*
 * Copyright 2000-2018 Vaadin Ltd.
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vaadin.data.provider.CallbackDataProvider;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridSortWhenPossible extends AbstractTestUI {

    private List<Person> persons;

    @Override
    protected void setup(VaadinRequest request) {
        persons = Collections.unmodifiableList(Arrays.asList(
                createPerson("a", 4, true), createPerson("b", 5, false),
                createPerson("c", 3, false), createPerson("a", 6, false),
                createPerson("a", 2, true), createPerson("c", 7, false),
                createPerson("b", 1, true)));

        CheckBox inMemoryCheckBox = new CheckBox("In memory");
        addComponent(inMemoryCheckBox);
        addComponent(new Button("Create Grid",
                e -> addComponent(getGrid(inMemoryCheckBox.getValue()))));
    }

    private final Grid<Person> getGrid(boolean inMemory) {
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setId("name");
        grid.addColumn(Person::getAge, new NumberRenderer()).setId("age");
        grid.addColumn(Person::getDeceased);

        if (inMemory) {
            grid.setItems(persons);
        } else {
            grid.setDataProvider(new CallbackDataProvider<>(query -> {
                List<Person> list = new ArrayList<>(persons);
                if (!query.getSortOrders().isEmpty()) {
                    QuerySortOrder order = query.getSortOrders().get(0);

                    Comparator<Person> comparator;
                    if ("name".equals(order.getSorted())) {
                        comparator = Comparator.comparing(Person::getFirstName);
                    } else {
                        comparator = Comparator.comparing(Person::getAge);
                    }

                    if (order.getDirection() == SortDirection.DESCENDING) {
                        comparator = comparator.reversed();
                    }
                    Collections.sort(list, comparator);
                }
                return list.stream();
            }, query -> persons.size()));
        }
        return grid;
    }

    private Person createPerson(String name, int age, boolean deceased) {
        Person person = new Person();
        person.setFirstName(name);
        person.setAge(age);
        person.setDeceased(deceased);
        return person;

    }

    @Override
    public String getTestDescription() {
        return "Grid columns are sorted, only when sorting is implemented";
    }

    @Override
    public Integer getTicketNumber() {
        return 8792;
    }
}
