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

import java.util.stream.IntStream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.VerticalLayout;

public class GridResizeAndScroll extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout content = new VerticalLayout();
        addComponent(content);

        final Grid<Person> grid = new Grid<>();
        content.setHeight("500px");
        content.addComponent(grid);

        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getLastName);
        grid.addColumn(Person::getEmail);

        grid.setItems(IntStream.range(0, 50).mapToObj(this::createPerson));

        grid.setSizeFull();

        grid.setSelectionMode(SelectionMode.MULTI);

        grid.addSelectionListener(event -> {
            if (event.getAllSelectedItems().isEmpty()) {
                grid.setHeight("100%");
            } else {
                grid.setHeight("50%");
            }
        });
    }

    private Person createPerson(int index) {
        Person person = new Person();
        person.setFirstName("cell " + index + " 0");
        person.setLastName("cell " + index + " 1");
        person.setEmail("cell " + index + " 2");
        return person;
    }
}
