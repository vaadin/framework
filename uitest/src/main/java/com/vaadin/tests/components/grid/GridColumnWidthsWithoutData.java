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
import java.util.EnumSet;
import java.util.List;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.VerticalLayout;

public class GridColumnWidthsWithoutData extends AbstractTestUI {

    private SelectionMode selectionMode = SelectionMode.NONE;
    private List<Person> items;
    private DataProvider<Person, SerializablePredicate<Person>> provider;
    private Grid<Person> grid = createGrid(true);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(grid);

        NativeSelect<SelectionMode> selectionModeSelector = new NativeSelect<>(
                "Selection mode", EnumSet.allOf(SelectionMode.class));
        selectionModeSelector.setValue(selectionMode);
        selectionModeSelector.addValueChangeListener(event -> {
            selectionMode = selectionModeSelector.getValue();
            grid.setSelectionMode(selectionMode);
        });

        addComponent(selectionModeSelector);

        addComponent(new Button("Recreate without data",
                event -> replaceGrid(createGrid(false))));

        addComponent(new Button("Recreate with data",
                event -> replaceGrid(createGrid(true))));

        addComponent(new Button("Add data", event -> addDataToGrid()));

        addComponent(new Button("Remove data", event -> {
            items.clear();
            provider.refreshAll();
        }));

    }

    private void replaceGrid(Grid<Person> newGrid) {
        ((VerticalLayout) grid.getParent()).replaceComponent(grid, newGrid);
        grid = newGrid;
    }

    private Grid<Person> createGrid(boolean withData) {
        Grid<Person> grid = new Grid<>();
        grid.addColumn(Person::getFirstName);
        grid.addColumn(Person::getLastName);
        grid.setWidth("300px");
        grid.setSelectionMode(selectionMode);

        items = new ArrayList<>();
        provider = DataProvider.ofCollection(items);
        grid.setDataProvider(provider);

        if (withData) {
            addDataToGrid();
        }

        return grid;
    }

    private void addDataToGrid() {
        Person person = new Person();
        person.setFirstName("Some");
        person.setLastName("Data with more data in one col");
        items.add(person);
        provider.refreshAll();
    }

}
