package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.util.Person;
import com.vaadin.tests.util.PersonContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class GridDetailsLocation extends AbstractTestUI {

    private TextField numberTextField;
    private Grid<Person> grid;
    private List<Person> testData;

    @Override
    protected void setup(VaadinRequest request) {

        grid = new Grid<>();
        testData = new ArrayList<>(PersonContainer.createTestData(1000));
        grid.setItems(testData);
        grid.addColumn(item -> item.getFirstName()).setCaption("First Name");
        grid.addColumn(item -> item.getLastName()).setCaption("Last Name");

        grid.setSelectionMode(Grid.SelectionMode.NONE);
        addComponent(grid);

        final CheckBox checkbox = new CheckBox("Details generator");
        checkbox.addValueChangeListener(event -> {
            if (checkbox.getValue()) {
                grid.setDetailsGenerator(person -> {
                    Label label = new Label(
                            person.getFirstName() + " " + person.getLastName());
                    // currently the decorator row doesn't change its height
                    // when the content height is different.
                    label.setHeight("30px");
                    return label;
                });
            } else {
                grid.setDetailsGenerator(null);
            }
        });
        addComponent(checkbox);

        numberTextField = new TextField("Row");
        addComponent(numberTextField);

        addComponent(new Button("Toggle and scroll", clickEvent -> {
            toggle();
            scrollTo();
        }));
        addComponent(new Button("Scroll and toggle", clickEvent -> {
            scrollTo();
            toggle();
        }));
    }

    private void toggle() {
        Person itemId = testData.get(Integer.parseInt(numberTextField.getValue()));
        boolean isVisible = grid.isDetailsVisible(itemId);
        grid.setDetailsVisible(itemId, !isVisible);
    }

    private void scrollTo() {
        grid.scrollTo(Integer.parseInt(numberTextField.getValue()));
    }
}
