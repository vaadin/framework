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

import java.util.stream.Collectors;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridColspans extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Person person = new Person();
        person.setFirstName("Rudolph");
        person.setLastName("Reindeer");
        person.setEmail("test@vaadin.com");
        person.setAge(34);
        person.setSalary(3210);

        Grid<Person> grid = new Grid<>();
        grid.setWidth("600px");

        Column<Person, String> firstNameColumn = grid
                .addColumn(Person::getFirstName);
        firstNameColumn.setId("firstName").setCaption("First name");
        Column<Person, String> lastNameColumn = grid
                .addColumn(Person::getLastName);
        lastNameColumn.setCaption("Last name");
        Column<Person, String> emailColumn = grid.addColumn(Person::getEmail);
        Column<Person, Number> ageColumn = grid.addColumn(Person::getAge,
                new NumberRenderer());
        ageColumn.setCaption("Age");
        ageColumn.setId("ageColumn");
        Column<Person, Number> salaryColumn = grid.addColumn(Person::getSalary,
                new NumberRenderer());

        grid.setItems(person);

        grid.setSelectionMode(SelectionMode.MULTI);
        addComponent(grid);

        HeaderRow row = grid.prependHeaderRow();
        row.join(row.getCell(firstNameColumn), row.getCell(lastNameColumn))
                .setText("Full Name");
        row.join(row.getCell(emailColumn), row.getCell(ageColumn),
                row.getCell(salaryColumn)).setText("Misc");
        grid.prependHeaderRow().join(grid.getColumns().stream()
                .map(row::getCell).collect(Collectors.toSet()))
                .setText("All the stuff");

        FooterRow footerRow = grid.appendFooterRow();
        footerRow.join(footerRow.getCell(firstNameColumn),
                footerRow.getCell(lastNameColumn)).setText("Full Name");
        footerRow.join(footerRow.getCell(emailColumn),
                footerRow.getCell(ageColumn), footerRow.getCell(salaryColumn))
                .setText("Misc");
        grid.appendFooterRow().join(grid.getColumns().stream()
                .map(footerRow::getCell).collect(Collectors.toSet()))
                .setText("All the stuff");

        addComponent(new Button("Show/Hide firstName", event -> {
            Column<Person, ?> column = grid.getColumn("firstName");
            if (column != null) {
                grid.removeColumn(column);
            } else {
                grid.addColumn(Person::getFirstName).setId("firstName").setCaption("First name");
            }
        }));

        addComponent(new Button("Change column order", event -> {
            grid.setColumnOrder(grid.getColumn("ageColumn"),
                    grid.getColumn("firstName"));
        }));
    }

    @Override
    protected String getTestDescription() {
        return "Grid header and footer colspans";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13334;
    }

}
