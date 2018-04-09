package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Theme;
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
        ListDataProvider<ComplexPerson> dataProvider = ComplexPerson
                .createDataProvider(100);

        String[] cities = dataProvider.fetch(new Query<>())
                .map(person -> person.getAddress().getCity()).distinct()
                .toArray(String[]::new);
        CustomCitySelect cityEditor = new CustomCitySelect(cities);

        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();

        Grid<ComplexPerson> grid = new Grid<>();
        grid.setWidth("800px");
        grid.addColumn(person -> person.getFirstName())
                .setId(FIRST_NAME_IDENTIFIER).setCaption("First Name")
                .setEditorComponent(firstNameField,
                        ComplexPerson::setFirstName);
        grid.addColumn(person -> person.getLastName())
                .setId(LAST_NAME_IDENTIFIER).setCaption("Last Name")
                .setEditorComponent(lastNameField, ComplexPerson::setLastName);
        grid.addColumn(person -> person.getAddress().getCity())
                .setId(ADDRESS_CITY_IDENTIFIER).setCaption("City Name")
                .setEditorComponent(cityEditor,
                        (person, city) -> person.getAddress().setCity(city));

        grid.getEditor().setEnabled(true);

        grid.setDataProvider(dataProvider);

        addComponent(grid);
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
