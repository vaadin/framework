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

import java.util.HashSet;
import java.util.Set;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Binder;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.Query;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;

@Theme("tests-valo-disabled-animations")
public class GridEditorCustomField extends AbstractTestUIWithLog {

    private static final String LAST_NAME_IDENTIFIER = "lastName";
    private static final String FIRST_NAME_IDENTIFIER = "firstName";
    private static final String ADDRESS_CITY_IDENTIFIER = "address.city";

    @Override
    protected void setup(VaadinRequest request) {
        Grid<ComplexPerson> grid = createGrid();

        ListDataProvider<ComplexPerson> dataProvider = ComplexPerson
                .createDataProvider(100);

        grid.setDataProvider(dataProvider);

        Set<String> cities = new HashSet<>();
        dataProvider.fetch(new Query<>()).forEach(person -> {
            cities.add(person.getAddress().getCity());
        });
        CustomCitySelect cityEditor = new CustomCitySelect(
                cities.toArray(new String[cities.size()]));

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        Binder<ComplexPerson> binder = new Binder<>();

        binder.bind(firstNameField, ComplexPerson::getFirstName,
                ComplexPerson::setFirstName);
        binder.bind(lastNameField, ComplexPerson::getLastName,
                ComplexPerson::setLastName);
        binder.bind(cityEditor, person -> person.getAddress().getCity(),
                (person, city) -> person.getAddress().setCity(city));

        grid.getEditor().setBinder(binder);
        grid.getColumn(ADDRESS_CITY_IDENTIFIER).setEditorComponent(cityEditor);
        grid.getColumn(FIRST_NAME_IDENTIFIER)
                .setEditorComponent(firstNameField);
        grid.getColumn(LAST_NAME_IDENTIFIER).setEditorComponent(lastNameField);

        addComponent(grid);
    }

    private Grid<ComplexPerson> createGrid() {
        Grid<ComplexPerson> grid = new Grid<>();
        grid.setWidth("800px");
        grid.addColumn(person -> person.getFirstName())
                .setId(FIRST_NAME_IDENTIFIER).setCaption("First Name");
        grid.addColumn(person -> person.getLastName())
                .setId(LAST_NAME_IDENTIFIER).setCaption("Last Name");
        grid.addColumn(person -> person.getAddress().getCity())
                .setId(ADDRESS_CITY_IDENTIFIER).setCaption("City Name");
        grid.getEditor().setEnabled(true);

        return grid;
    }

    public static class CustomCitySelect extends CustomField<String> {
        private HorizontalLayout fieldLayout;
        private String[] values;
        private ComboBox<String> cityComboBox;
        private String cachedValue;

        public CustomCitySelect(String... values) {
            this.values = values;
        }

        @Override
        protected Component initContent() {
            fieldLayout = new HorizontalLayout();
            fieldLayout.setWidth("100%");

            cityComboBox = new ComboBox<>();
            cityComboBox.setItems(values);
            if (cachedValue != null) {
                cityComboBox.setValue(cachedValue);
                cachedValue = null;
            }

            fieldLayout.addComponent(cityComboBox);
            fieldLayout.setExpandRatio(cityComboBox, 1.0f);

            Button addCountryButton = new Button("New");
            fieldLayout.addComponent(addCountryButton);

            setFocusDelegate(cityComboBox);

            return fieldLayout;
        }

        @Override
        public String getValue() {
            if (cityComboBox == null) {
                return null;
            }
            return cityComboBox.getValue();
        }

        @Override
        protected void doSetValue(String value) {
            if (cityComboBox == null) {
                getContent();
            }
            cityComboBox.setValue(value);
        }
    }

}
