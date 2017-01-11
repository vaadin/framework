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
import java.util.Collection;
import java.util.Random;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.TestDataGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridEditorUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = createGrid();
        grid.setItems(createTestData());
        addComponent(grid);
    }

    protected Collection<Person> createTestData() {
        return createTestData(100);
    }

    protected Collection<Person> createTestData(int size) {
        Random r = new Random(0);
        ArrayList<Person> testData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Person person = new Person();
            person.setFirstName(TestDataGenerator.getFirstName(r));
            person.setLastName(TestDataGenerator.getLastName(r));
            person.getAddress().setCity(TestDataGenerator.getCity(r));
            person.setEmail(person.getFirstName().toLowerCase() + "."
                    + person.getLastName().toLowerCase() + "@vaadin.com");
            person.setPhoneNumber(TestDataGenerator.getPhoneNumber(r));

            person.getAddress()
                    .setPostalCode(TestDataGenerator.getPostalCode(r));
            person.getAddress()
                    .setStreetAddress(TestDataGenerator.getStreetAddress(r));
            testData.add(person);
        }
        return testData;
    }

    protected Grid<Person> createGrid() {
        Grid<Person> grid = new Grid<>();

        Binder<Person> binder = new Binder<>();
        grid.getEditor().setBinder(binder);

        grid.addColumn(Person::getEmail).setCaption("Email");
        Column<Person, String> fistNameColumn = grid
                .addColumn(Person::getFirstName).setCaption("First Name");
        Column<Person, String> lastNameColumn = grid
                .addColumn(Person::getLastName).setCaption("Last Name");

        Column<Person, String> phoneColumn = grid
                .addColumn(Person::getPhoneNumber).setCaption("Phone Number");
        grid.addColumn(person -> person.getAddress().getStreetAddress())
                .setCaption("Street Address");
        grid.addColumn(person -> person.getAddress().getPostalCode(),
                new NumberRenderer()).setCaption("Postal Code");
        grid.addColumn(person -> person.getAddress().getCity())
                .setCaption("City");

        grid.getEditor().setEnabled(true);

        PasswordField passwordField = new PasswordField();
        fistNameColumn.setEditorComponent(passwordField);
        binder.bind(passwordField, Person::getFirstName, Person::setFirstName);

        TextField lastNameEditor = new TextField();
        lastNameColumn.setEditorComponent(lastNameEditor);
        lastNameEditor.setMaxLength(50);
        binder.bind(lastNameEditor, Person::getLastName, Person::setLastName);

        TextField phoneEditor = new TextField();
        phoneEditor.setReadOnly(true);
        phoneColumn.setEditorComponent(phoneEditor);
        binder.bind(phoneEditor, Person::getPhoneNumber,
                Person::setPhoneNumber);

        return grid;
    }

}
