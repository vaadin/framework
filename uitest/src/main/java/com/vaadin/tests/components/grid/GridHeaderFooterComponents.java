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
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridHeaderFooterComponents extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = new Grid<>();
        grid.setWidth("800px");
        grid.addColumn(Person::getFirstName).setId("action");
        grid.addColumn(Person::getLastName).setId("string");
        grid.addColumn(Person::getAge, new NumberRenderer()).setId("int");
        grid.addColumn(Person::getSalaryDouble, new NumberRenderer())
                .setId("double");

        grid.setItems(IntStream.range(0, 5).mapToObj(this::createPerson));

        final HeaderRow defaultRow = grid.getDefaultHeaderRow();
        final HeaderRow toggleVisibilityRow = grid.appendHeaderRow();
        final HeaderRow filterRow = grid.appendHeaderRow();

        final FooterRow footerRow = grid.appendFooterRow();
        final FooterRow toggleVisibilityFooterRow = grid.addFooterRowAt(0);
        final FooterRow filterFooterRow = grid.addFooterRowAt(0);

        // Set up a filter for all columns

        for (Column<Person, ?> column : grid.getColumns()) {
            final HeaderCell headerCell = filterRow.getCell(column);
            final FooterCell footerCell = filterFooterRow.getCell(column);

            headerCell.setComponent(createTextField(column.getId()));
            footerCell.setComponent(createTextField(column.getId()));

            toggleVisibilityRow.getCell(column.getId())
                    .setComponent(new Button("Toggle field", event -> {
                        Component c = headerCell.getComponent();
                        c.setVisible(!c.isVisible());
                    }));
            toggleVisibilityFooterRow.getCell(column.getId())
                    .setComponent(new Button("Toggle field", event -> {
                        Component c = footerCell.getComponent();
                        c.setVisible(!c.isVisible());
                    }));
        }

        addComponent(grid);

        addRemoveHeaderRow(grid, defaultRow);
        addRemoveHeaderRow(grid, filterRow);
        addRemoveHeaderRow(grid, toggleVisibilityRow);

        addRemoveFooterRow(grid, footerRow);
        addRemoveFooterRow(grid, filterFooterRow);
        addRemoveFooterRow(grid, toggleVisibilityFooterRow);

        // Hide first field initially
        filterRow.getCell("string").getComponent().setVisible(false);
        filterFooterRow.getCell("string").getComponent().setVisible(false);
    }

    private void addRemoveHeaderRow(final Grid<Person> grid,
            final HeaderRow row) {
        row.getCell("action").setComponent(
                new Button("Remove row", event -> grid.removeHeaderRow(row)));

    }

    private void addRemoveFooterRow(final Grid<Person> grid,
            final FooterRow row) {
        row.getCell("action").setComponent(
                new Button("Remove row", event -> grid.removeFooterRow(row)));
    }

    private Person createPerson(int i) {
        Person person = new Person();
        person.setFirstName("");
        person.setLastName("Hello world");
        person.setAge(13);
        person.setSalaryDouble(5.2d);
        return person;
    }

    private TextField createTextField(final Object pid) {
        TextField filterField = new TextField();
        filterField.setWidth("8em");
        filterField.setValue("Filter: " + pid);
        filterField.addValueChangeListener(listener -> {
            log("value change for field in " + pid + " to "
                    + listener.getValue());
        });
        return filterField;
    }

}
