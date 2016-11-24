package com.vaadin.tests.components.grid;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MoveGridAndAddRow extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();

        final VerticalLayout anotherLayout = new VerticalLayout();
        anotherLayout.addComponent(new Label("This is another layout"));
        final Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity()).setCaption("A");
        List<String> items = new ArrayList<>();
        items.add("1");
        grid.setItems(items);

        final Button button = new Button("Add row and remove this button");
        button.setId("add");
        button.addClickListener(event -> {
            items.add("2");
            grid.setItems(items);
            button.setVisible(false);
        });

        Button move = new Button("Move grid to other layout");
        move.setId("move");
        move.addClickListener(event -> anotherLayout.addComponent(grid));

        layout.addComponents(button, move, grid);
        addComponent(new HorizontalLayout(layout, anotherLayout));

    }
}
