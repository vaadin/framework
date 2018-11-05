package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.TestDataGenerator;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridEditorUI extends AbstractReindeerTestUIWithLog {

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
        Column<Person, String> fistNameColumn = grid
                .addColumn(Person::getFirstName).setCaption("First Name")
                .setId("firstName");
        Column<Person, String> lastNameColumn = grid
                .addColumn(Person::getLastName).setCaption("Last Name")
                .setId("lastName");

        Column<Person, String> phoneColumn = grid
                .addColumn(Person::getPhoneNumber).setCaption("Phone Number")
                .setId("phone");
        grid.addColumn(person -> person.getAddress().getStreetAddress())
                .setCaption("Street Address").setId("street");
        grid.addColumn(person -> person.getAddress().getPostalCode(),
                new NumberRenderer()).setCaption("Postal Code").setId("zip");
        grid.addColumn(person -> person.getAddress().getCity())
                .setCaption("City").setId("city");

        grid.getEditor().setEnabled(true);

        PasswordField passwordField = new PasswordField();
        fistNameColumn.setEditorComponent(passwordField, Person::setFirstName);

        TextField lastNameEditor = new TextField();
        lastNameColumn.setEditorComponent(lastNameEditor, Person::setLastName);
        lastNameEditor.setMaxLength(50);

        TextField phoneEditor = new TextField();
        phoneEditor.setReadOnly(true);
        phoneColumn.setEditorComponent(phoneEditor, Person::setPhoneNumber);

        return grid;
    }

}
