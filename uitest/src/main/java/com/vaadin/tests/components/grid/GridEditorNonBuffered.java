package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.TestDataGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridEditorNonBuffered extends AbstractReindeerTestUIWithLog {

    final static String VALIDATION_ERROR_MESSAGE = "Validator error. Name cannot be empty";

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
        List<Person> testData = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Person person = new Person();
            person.setFirstName(TestDataGenerator.getFirstName(r));
            person.setLastName(TestDataGenerator.getLastName(r));
            person.getAddress().setCity(TestDataGenerator.getCity(r));
            person.setEmail(person.getFirstName().toLowerCase(Locale.ROOT) + "."
                    + person.getLastName().toLowerCase(Locale.ROOT)
                    + "@vaadin.com");
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

        grid.addColumn(Person::getEmail).setCaption("Email").setId("email");

        Column<Person, String> firstNameColumn = grid
                .addColumn(Person::getFirstName).setCaption("First Name")
                .setId("firstName");
        Column<Person, String> lastNameColumn = grid
                .addColumn(Person::getLastName).setCaption("Last Name")
                .setId("lastName");

        grid.addColumn(Person::getPhoneNumber).setCaption("Phone Number")
                .setId("phone");
        grid.addColumn(person -> person.getAddress().getStreetAddress())
                .setCaption("Street Address").setId("street");
        grid.addColumn(person -> person.getAddress().getPostalCode(),
                new NumberRenderer()).setCaption("Postal Code").setId("zip");
        grid.addColumn(person -> person.getAddress().getCity())
                .setCaption("City").setId("city");

        Binder<Person> binder = new Binder<>();

        TextField firstNameEditor = new TextField();
        Binding<Person, String> firstNamebinding = binder
                .forField(firstNameEditor)
                .withValidator(v -> (v != null && !v.isEmpty()),
                        VALIDATION_ERROR_MESSAGE)
                .bind(Person::getFirstName, Person::setFirstName);
        firstNameColumn.setEditorBinding(firstNamebinding);


        TextField lastNameEditor = new TextField();
        Binding<Person, String> lastNamebinding = binder
                .forField(lastNameEditor)
                .withValidator(v -> (v != null && !v.isEmpty()),
                        VALIDATION_ERROR_MESSAGE)
                .bind(Person::getLastName, Person::setLastName);
        lastNameColumn.setEditorBinding(lastNamebinding);

        grid.getEditor().setBuffered(false);
        grid.getEditor().setEnabled(true);
        grid.getEditor().setBinder(binder);

        return grid;
    }

}
