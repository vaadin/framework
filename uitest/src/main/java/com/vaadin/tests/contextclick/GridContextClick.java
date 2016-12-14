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
package com.vaadin.tests.contextclick;

import java.util.Collections;

import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.GridContextClickEvent;
import com.vaadin.ui.HorizontalLayout;

public class GridContextClick extends
        AbstractContextClickUI<Grid<Person>, GridContextClickEvent<Person>> {

    @Override
    protected Grid<Person> createTestComponent() {
        Grid<Person> grid = new Grid<>();
        grid.setItems(PersonContainer.createTestData());
        grid.addColumn(person -> String.valueOf(person.getAddress()))
                .setCaption("Address");
        grid.addColumn(person -> String.valueOf(person.getEmail()))
                .setCaption("Email");
        grid.addColumn(person -> String.valueOf(person.getFirstName()))
                .setCaption("First Name");
        grid.addColumn(person -> String.valueOf(person.getLastName()))
                .setCaption("Last Name");
        grid.addColumn(person -> String.valueOf(person.getPhoneNumber()))
                .setCaption("Phone Number");
        grid.addColumn(person -> String
                .valueOf(person.getAddress().getStreetAddress()))
                .setCaption("Street Address");
        grid.addColumn(person -> String.valueOf(person.getAddress().getCity()))
                .setCaption("City");

        // grid.setFooterVisible(true);
        // grid.appendFooterRow();

        // grid.setColumnOrder("address", "email", "firstName", "lastName",
        // "phoneNumber", "address.streetAddress", "address.postalCode",
        // "address.city");

        grid.setWidth("100%");
        grid.setHeight("400px");

        return grid;
    }

    @Override
    protected void handleContextClickEvent(
            GridContextClickEvent<Person> event) {
        String value = "";
        Person person = event.getItem();
        if (event.getItem() != null) {
            value = person.getFirstName() + " " + person.getLastName();
        } else if (event.getSection() == Section.HEADER) {
            value = event.getColumn().getCaption();
        } else if (event.getSection() == Section.FOOTER) {
            value = event.getColumn().getCaption();
        }

        if (event.getColumn() != null) {
            log("ContextClickEvent value: " + value + ", column: "
                    + event.getColumn().getCaption() + ", section: "
                    + event.getSection());
        } else {
            log("ContextClickEvent value: " + value + ", section: "
                    + event.getSection());

        }
    }

    @Override
    protected HorizontalLayout createContextClickControls() {
        HorizontalLayout controls = super.createContextClickControls();
        controls.addComponent(
                new Button("Remove all content", new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        testComponent.setItems(Collections.emptyList());
                    }
                }));
        return controls;
    }
}
