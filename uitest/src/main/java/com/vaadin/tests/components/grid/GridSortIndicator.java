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

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridSortIndicator extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = getGrid();
        addComponent(grid);
        addComponent(new Button("Sort first", event -> grid
                .sort(grid.getColumn("name"), SortDirection.ASCENDING)));
        addComponent(new Button("Sort both",
                event -> grid
                        .setSortOrder(GridSortOrder.asc(grid.getColumn("name"))
                        .thenAsc(grid.getColumn("age")))));
    }

    private final Grid<Person> getGrid() {
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName).setId("name");
        grid.addColumn(Person::getAge, new NumberRenderer()).setId("age");

        grid.setItems(createPerson("a", 4), createPerson("b", 5),
                createPerson("c", 3), createPerson("a", 6),
                createPerson("a", 2), createPerson("c", 7),
                createPerson("b", 1));
        return grid;
    }

    private Person createPerson(String name, int age) {
        Person person = new Person();
        person.setFirstName(name);
        person.setAge(age);
        return person;
    }

    @Override
    public String getTestDescription() {
        return "UI to test server-side sorting of grid columns "
                + "and displaying sort indicators";
    }

    @Override
    public Integer getTicketNumber() {
        return 17440;
    }
}
