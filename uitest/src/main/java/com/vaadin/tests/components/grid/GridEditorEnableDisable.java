package com.vaadin.tests.components.grid;

import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;

public class GridEditorEnableDisable extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = new Grid<>();
        Person person1 = new Person();
        person1.setFirstName("Foo");

        Person person2 = new Person();
        person2.setFirstName("Bar");

        grid.setItems(person1, person2);
        Grid.Column<Person, String> column = grid
                .addColumn(Person::getFirstName);

        Binder<Person> binder = grid.getEditor().getBinder();
        grid.getEditor().setEnabled(true);

        TextField field = new TextField();
        Binder.Binding<Person, String> binding = binder.bind(field,
                Person::getFirstName, Person::setFirstName);
        column.setEditorBinding(binding);

        addComponent(grid);

        final Button disableButton = new Button("Disable");
        disableButton.addClickListener((event) -> {
            grid.getEditor().setEnabled(false);
        });

        addComponent(disableButton);

        final Button cancelAndDisableButton = new Button("Cancel & Disable");
        cancelAndDisableButton.addClickListener((event) -> {
            if (grid.getEditor().isOpen())
                grid.getEditor().cancel();
            grid.getEditor().setEnabled(false);
        });

        addComponent(cancelAndDisableButton);

        final Button enableButton = new Button("Enable");
        enableButton.addClickListener((event) -> {
            grid.getEditor().setEnabled(true);
        });

        addComponent(enableButton);

        final Button enableAndEditRowButton = new Button("Enable & Edit Row");
        enableAndEditRowButton.addClickListener((event) -> {
            grid.getEditor().setEnabled(true);
            grid.getEditor().editRow(0);
        });

        addComponent(enableAndEditRowButton);
    }

}
