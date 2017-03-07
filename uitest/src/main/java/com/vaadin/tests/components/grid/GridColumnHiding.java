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

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.v7.ui.Label;

public class GridColumnHiding extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();
        Column<Person, String> nameColumn = grid.addColumn(Person::getFirstName)
                .setHidable(true).setCaption("Name");
        Column<Person, Integer> ageColumn = grid
                .addColumn(Person::getAge, new NumberRenderer())
                .setHidable(true)
                .setHidingToggleCaption("custom age column caption")
                .setCaption("Age");
        Column<Person, String> emailColumn = grid.addColumn(Person::getEmail)
                .setCaption("Email");

        Button toggleNameColumn = new Button("server side toggle name column");
        Button toggleAgeColumn = new Button("server side toggle age column");
        Button toggleEmailColumn = new Button(
                "server side toggle email column");

        toggleNameColumn.addClickListener(
                event -> nameColumn.setHidden(!nameColumn.isHidden()));
        toggleAgeColumn.addClickListener(
                event -> ageColumn.setHidden(!ageColumn.isHidden()));
        toggleEmailColumn.addClickListener(
                event -> emailColumn.setHidden(!emailColumn.isHidden()));

        Label visibilityChangeLabel = new Label("visibility change label");
        grid.addColumnVisibilityChangeListener(event -> visibilityChangeLabel
                .setValue(event.getColumn().isHidden() + ""));

        Button toggleHidden = new Button("Toggle all column hidden state",
                event -> grid.getColumns().forEach(
                        column -> column.setHidden(!column.isHidden())));

        grid.setItems(Arrays.asList(Person.createTestPerson1(),
                Person.createTestPerson2()));

        addComponents(grid, toggleNameColumn, toggleAgeColumn,
                toggleEmailColumn, visibilityChangeLabel, toggleHidden);
    }
}
