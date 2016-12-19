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

import com.vaadin.data.provider.AppendableSearcher;
import com.vaadin.data.provider.DataProvider;
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

public class SearcherUI extends AbstractTestUI {
    private final ListDataProvider<Person> data = DataProvider
            .create(PersonContainer.createTestData());

    private final AppendableSearcher<Person, SerializablePredicate<Person>> searcher = new AppendableSearcher<>(
            data);

    @Override
    protected void setup(VaadinRequest request) {
        TextField firstNameSearch = new TextField("First name");
        TextField lastNameSearch = new TextField("Last name");

        FormLayout searchForm = new FormLayout(firstNameSearch, lastNameSearch);
        searchForm.setCaption("Search form");

        Button searchButton = new Button("Search", e -> {
            searcher.clearFilters();
            if (!firstNameSearch.isEmpty()) {
                String firstName = firstNameSearch.getValue();
                searcher.addFilter(person -> {
                    return person.getFirstName().contains(firstName);
                });
            }

            if (!lastNameSearch.isEmpty()) {
                String lastName = lastNameSearch.getValue();
                searcher.addFilter(person -> {
                    return person.getLastName().contains(lastName);
                });
            }
        });
        searchButton.setClickShortcut(KeyCode.ENTER);

        Grid<Person> grid = new Grid<>();
        grid.setCaption("Grid");
        grid.setDataProvider(searcher);
        Column<Person, String> col = grid.addColumn(Person::getFirstName)
                .setCaption("First name");
        grid.addColumn(Person::getLastName).setCaption("Last name");

        ComboBox<Person> comboBox = new ComboBox<>("Combo box");
        // Explicitly only filter by first name
        comboBox.setDataProvider(searcher.convertFilter(
                text -> person -> person.getFirstName().contains(text)));
        comboBox.setItemCaptionGenerator(
                person -> person.getFirstName() + " " + person.getLastName());

        addComponents(searchForm, searchButton, grid, comboBox,
                new Button("Update", e -> grid.getHeaderRow(0).getCell(col)
                        .setComponent(new TextField())));
    }

}
