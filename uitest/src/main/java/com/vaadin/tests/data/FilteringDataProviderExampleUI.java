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
package com.vaadin.tests.data;

import com.vaadin.data.provider.AppendableFilteringDataProvider;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.FilteringDataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;

/**
 * UI showing an example of how {@link FilteringDataProvider} is used in
 * practice. There's no TestBench tests using this UI since the actual
 * functionality is already covered by unit tests.
 */
public class FilteringDataProviderExampleUI extends AbstractTestUI {
    private final ListDataProvider<Person> data = DataProvider
            .create(PersonContainer.createTestData());

    private final AppendableFilteringDataProvider<Person, SerializablePredicate<Person>> filtering = new AppendableFilteringDataProvider<>(
            data);

    @Override
    protected void setup(VaadinRequest request) {
        TextField firstNameSearch = new TextField("First name");
        TextField lastNameSearch = new TextField("Last name");

        FormLayout searchForm = new FormLayout(firstNameSearch, lastNameSearch);
        searchForm.setCaption("Search form");

        Button searchButton = new Button("Search", e -> {
            filtering.clearFilters();
            if (!firstNameSearch.isEmpty()) {
                String firstName = firstNameSearch.getValue();
                filtering.addFilter(
                        person -> person.getFirstName().contains(firstName));
            }

            if (!lastNameSearch.isEmpty()) {
                String lastName = lastNameSearch.getValue();
                filtering.addFilter(
                        person -> person.getLastName().contains(lastName));
            }
        });
        searchButton.setClickShortcut(KeyCode.ENTER);

        /*
         * Grid that shows the items that match the conditions in the search
         * form.
         */
        Grid<Person> grid = new Grid<>();
        grid.setCaption("Grid");
        grid.setDataProvider(filtering);
        Column<Person, String> col = grid.addColumn(Person::getFirstName)
                .setCaption("First name");
        grid.addColumn(Person::getLastName).setCaption("Last name");

        /*
         * Combo box that shows the items that match the conditions in the
         * search form, combined with filtering based on the text entered into
         * the combo box.
         */
        ComboBox<Person> comboBox = new ComboBox<>("Combo box");
        // Explicitly only filter by first name
        comboBox.setDataProvider(filtering.convertFilter(
                text -> person -> person.getFirstName().contains(text)));
        comboBox.setItemCaptionGenerator(
                person -> person.getFirstName() + " " + person.getLastName());

        addComponents(searchForm, searchButton, grid, comboBox,
                new Button("Update", e -> grid.getHeaderRow(0).getCell(col)
                        .setComponent(new TextField())));
    }

}
