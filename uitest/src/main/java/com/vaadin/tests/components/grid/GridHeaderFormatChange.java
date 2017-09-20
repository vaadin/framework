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

import com.vaadin.data.SelectionModel;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Address;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.components.grid.HeaderRow;

public class GridHeaderFormatChange extends AbstractReindeerTestUI {

    private static final long serialVersionUID = -2787771187365766027L;

    private HeaderRow row;

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();
        grid.setWidth("600px");

        grid.addColumn(Person::getFirstName).setId("firstName");
        grid.addColumn(Person::getLastName).setId("lastName");
        grid.addColumn(person -> person.getAddress().getStreetAddress())
                .setId("streetAddress");
        grid.addColumn(person -> person.getAddress().getPostalCode())
                .setId("zip");
        grid.addColumn(person -> person.getAddress().getCity()).setId("city");
        grid.setSelectionMode(SelectionMode.SINGLE);
        addComponent(grid);

        grid.setItems(createPerson());

        Button showHide = new Button("Hide firstName", event -> {
            Column<Person, ?> column = grid.getColumn("firstName");
            if (grid.getColumn("firstName") != null) {
                grid.removeColumn(column);
                event.getButton().setCaption("Show firstName");
            } else {
                grid.addColumn(Person::getFirstName).setId("firstName");
                grid.setColumnOrder(grid.getColumn("firstName"),
                        grid.getColumn("lastName"),
                        grid.getColumn("streetAddress"), grid.getColumn("zip"),
                        grid.getColumn("city"));

                event.getButton().setCaption("Hide firstName");
            }
        });
        showHide.setId("show_hide");

        Button selectionMode = new Button("Set multiselect", event -> {
            if (grid.getSelectionModel() instanceof SelectionModel.Single) {
                grid.setSelectionMode(SelectionMode.MULTI);
            } else {
                grid.setSelectionMode(SelectionMode.SINGLE);
            }
        });
        selectionMode.setId("selection_mode");

        Button join = new Button("Add Join header column", event -> {
            if (row == null) {
                row = grid.prependHeaderRow();
                if (grid.getColumn("firstName") != null) {
                    row.join(
                            grid.getDefaultHeaderRow()
                                    .getCell(grid.getColumn("firstName")),
                            grid.getDefaultHeaderRow()
                                    .getCell(grid.getColumn("lastName")))
                            .setText("Full Name");
                }
                row.join(
                        grid.getDefaultHeaderRow()
                                .getCell(grid.getColumn("streetAddress")),
                        grid.getDefaultHeaderRow()
                                .getCell(grid.getColumn("zip")),
                        grid.getDefaultHeaderRow()
                                .getCell(grid.getColumn("city")))
                        .setText("Address");
            } else {
                grid.removeHeaderRow(row);
                row = null;
            }
        });
        join.setId("join");
        addComponent(new HorizontalLayout(showHide, selectionMode, join));
    }

    @Override
    protected String getTestDescription() {
        return "Grid for testing header re-rendering.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17131;
    }

    private Person createPerson() {
        Person person = new Person();
        person.setFirstName("Rudolph");
        person.setLastName("Reindeer");
        person.setAddress(new Address());

        person.getAddress().setStreetAddress("Ruukinkatu 2-4");
        person.getAddress().setPostalCode(20540);
        person.getAddress().setCity("Turku");
        return person;
    }
}
