package com.vaadin.tests.components.grid;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;

/**
 * @author Vaadin Ltd
 *
 */
public class GridEditorEvents extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();
        Person person1 = new Person();
        person1.setFirstName("");

        Person person2 = new Person();
        person2.setFirstName("foo");

        grid.setItems(person1, person2);
        Column<Person, String> column = grid.addColumn(Person::getFirstName);

        Binder<Person> binder = grid.getEditor().getBinder();
        grid.getEditor().setEnabled(true);

        TextField field = new TextField();
        Binding<Person, String> binding = binder.bind(field,
                Person::getFirstName, Person::setFirstName);
        column.setEditorBinding(binding);

        grid.getEditor().addOpenListener(event -> log("editor is opened"));
        grid.getEditor().addCancelListener(event -> log("editor is canceled"));
        grid.getEditor().addSaveListener(event -> log("editor is saved"));
        addComponent(grid);
    }

}
