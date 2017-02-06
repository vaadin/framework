package com.vaadin.tests.components.grid;

import java.util.Arrays;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.NumberRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridColumnResizing extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TextField input = new TextField();
        Label isResizedLabel = new Label("not resized");
        Grid<Person> grid = new Grid<>();
        Column<Person, String> nameColumn = grid.addColumn(Person::getFirstName)
                .setCaption("Name");
        Column<Person, Integer> ageColumn = grid
                .addColumn(Person::getAge, new NumberRenderer())
                .setCaption("Age");
        grid.addColumnResizeListener(event -> {
            if (event.isUserOriginated()) {
                isResizedLabel.setValue("client resized");
            } else {
                isResizedLabel.setValue("server resized");
            }
        });
        grid.setItems(Arrays.asList(Person.createTestPerson1(),
                Person.createTestPerson2()));

        addComponent(input);
        addButton("set width", event -> nameColumn
                .setWidth(Double.parseDouble(input.getValue())));
        addButton("set expand ratio", event -> {
            nameColumn.setExpandRatio(4);
            ageColumn.setExpandRatio(1);
        });
        addButton("set min width", event -> nameColumn
                .setMinimumWidth(Double.parseDouble(input.getValue())));
        addButton("set max width", event -> nameColumn
                .setMaximumWidth(Double.parseDouble(input.getValue())));
        addButton("toggle resizable",
                event -> nameColumn.setResizable(!nameColumn.isResizable()));

        addComponents(grid, isResizedLabel);
    }

}
