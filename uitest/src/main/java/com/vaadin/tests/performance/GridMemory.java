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
package com.vaadin.tests.performance;

import java.util.List;
import java.util.Optional;

import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;

/**
 * @author Vaadin Ltd
 *
 */
public class GridMemory extends AbstractBeansMemoryTest<Grid<Person>> {

    @Override
    protected Grid<Person> createComponent() {
        Grid<Person> grid = new Grid<>();
        grid.addColumn("First Name", Person::getFirstName);
        grid.addColumn("Last Name", Person::getLastName);
        grid.addColumn("Street",
                person -> Optional.ofNullable(person.getAddress())
                        .map(Address::getStreetAddress).orElse(null));
        grid.addColumn("Zip", person -> Optional.ofNullable(person.getAddress())
                .map(Address::getPostalCode).map(Object::toString).orElse(""));
        grid.addColumn("City",
                person -> Optional.ofNullable(person.getAddress())
                        .map(Address::getCity).orElse(null));
        return grid;
    }

    @Override
    protected void setInMemoryContainer(Grid<Person> grid,
            List<Person> persons) {
        grid.setItems(persons);
    }

    @Override
    protected void setBackendContainer(Grid<Person> component,
            List<Person> data) {
        throw new UnsupportedOperationException();
    }

}
