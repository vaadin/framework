package com.vaadin.tests.contextclick;

import java.util.Collections;

import com.vaadin.shared.ui.grid.GridConstants.Section;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
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

        grid.setFooterVisible(true);
        grid.appendFooterRow();

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
                new Button("Remove all content", event -> testComponent
                        .setItems(Collections.emptyList())));
        return controls;
    }
}
