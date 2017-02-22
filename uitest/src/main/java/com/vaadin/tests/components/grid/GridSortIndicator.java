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

import com.vaadin.data.provider.GridSortOrder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.NumberRenderer;

/*
 * Test UI for checking that sort indicators of a Grid are updated when the sort order is changed by a
 * SortListener.
 */
public class GridSortIndicator extends AbstractReindeerTestUI {

    private SortDirection oldSortDirection = null;

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = getGrid();
        addComponent(grid);
        grid.addSortListener(order -> {
            ArrayList<GridSortOrder<Person>> currentSortOrder = new ArrayList<>(
                    order.getSortOrder());
            if (currentSortOrder.size() == 1) {
                // If the name column was clicked, set a new sort order for
                // both columns. Otherwise, revert to oldSortDirection if it
                // is not null.
                List<GridSortOrder<Person>> newSortOrder = new ArrayList<>();
                SortDirection newSortDirection = oldSortDirection;
                if (currentSortOrder.get(0).getSorted().getId()
                        .equals("name")) {
                    newSortDirection = SortDirection.ASCENDING
                            .equals(oldSortDirection) ? SortDirection.DESCENDING
                                    : SortDirection.ASCENDING;
                }
                if (newSortDirection != null) {
                    newSortOrder.add(new GridSortOrder<>(grid.getColumn("name"),
                            newSortDirection));
                    newSortOrder.add(new GridSortOrder<>(grid.getColumn("age"),
                            newSortDirection));
                    grid.setSortOrder(newSortOrder);
                }
                oldSortDirection = newSortDirection;
            }
        });
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
        return "When the first column is the primary sort column, both columns should have "
                + "a sort indicator with the same sort direction. Clicking on the right column "
                + "in that state should have no effect.";
    }

    @Override
    public Integer getTicketNumber() {
        return 17440;
    }
}
