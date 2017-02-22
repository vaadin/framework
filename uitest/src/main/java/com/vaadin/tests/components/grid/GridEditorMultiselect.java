package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.data.Binder;
import com.vaadin.data.Binder.Binding;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;

public class GridEditorMultiselect extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Person> grid = new Grid<>();

        Column<Person, String> nameColumn = grid.addColumn(Person::getFirstName)
                .setCaption("name");
        Column<Person, Integer> ageColumn = grid
                .addColumn(Person::getAge, new NumberRenderer())
                .setCaption("age");

        Binder<Person> binder = grid.getEditor().getBinder();

        nameColumn.setEditorComponent(new TextField(), Person::setFirstName);

        Binding<Person, Integer> ageBinding = binder.forField(new TextField())
                .withConverter(new StringToIntegerConverter(""))
                .bind(Person::getAge, Person::setAge);
        ageColumn.setEditorBinding(ageBinding);

        grid.setItems(IntStream.range(0, 30).mapToObj(this::createPerson));

        grid.getEditor().setEnabled(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);

        addComponent(grid);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17132;
    }

    @Override
    protected String getTestDescription() {
        return "Grid Multiselect: Edit mode allows invalid selection";
    }

    private Person createPerson(int i) {
        Person person = new Person();
        person.setFirstName("name" + i);
        person.setAge(i);
        return person;
    }
}
