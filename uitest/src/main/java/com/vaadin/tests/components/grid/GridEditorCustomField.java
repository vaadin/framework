/*
 * Copyright 2000-2014 Vaadin Ltd.
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
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;

@Theme("valo")
public class GridEditorCustomField extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new PersonTestGrid(100);
        grid.setWidth("800px");
        grid.setColumns("firstName", "lastName", "address.city");
        grid.setEditorEnabled(true);
        Set<String> cities = new HashSet<String>();
        for (Object o : grid.getContainerDataSource().getItemIds()) {
            ComplexPerson p = (ComplexPerson) o;
            cities.add(p.getAddress().getCity());
        }
        CustomCitySelect cityEditor = new CustomCitySelect(
                cities.toArray(new String[cities.size()]));
        grid.getColumn("address.city").setEditorField(cityEditor);
        addComponent(grid);
    }

    public static class CustomCitySelect extends CustomField<String> {
        private HorizontalLayout fieldLayout;
        private String[] values;
        private ComboBox cityComboBox;

        public CustomCitySelect(String... values) {
            this.values = values;
        }

        @Override
        protected Component initContent() {
            fieldLayout = new HorizontalLayout();
            fieldLayout.setWidth("100%");

            cityComboBox = new ComboBox();
            for (String value : values) {
                cityComboBox.addItem(value);
            }
            fieldLayout.addComponent(cityComboBox);
            fieldLayout.setExpandRatio(cityComboBox, 1.0f);

            Button addCountryButton = new Button("New");
            fieldLayout.addComponent(addCountryButton);

            setFocusDelegate(cityComboBox);

            return fieldLayout;
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }

        @Override
        protected void setInternalValue(String newValue) {
            super.setInternalValue(newValue);
            if (cityComboBox == null) {
                return;
            }
            cityComboBox.setValue(newValue);
        }

        @Override
        public String getInternalValue() {
            if (cityComboBox == null) {
                return null;
            }
            return (String) cityComboBox.getValue();
        }
    }

}
