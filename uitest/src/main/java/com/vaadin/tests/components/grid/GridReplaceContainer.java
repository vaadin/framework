package com.vaadin.tests.components.grid;

import java.util.Optional;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.data.bean.Person;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;

public class GridReplaceContainer extends SimpleGridUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Grid<Person> grid = createGrid();

        grid.setSelectionMode(SelectionMode.SINGLE);

        grid.addSelectionListener(event -> {
            Optional<Person> selected = event.getFirstSelectedItem();
            if (selected.isPresent()) {
                log("Now selected: " + selected.get().getAge());
            } else {
                log("Now selected: null");
            }
        });

        addComponent(grid);
        Button b = new Button("Re-set data source",
                event -> grid.setItems(createPersons()));
        addComponent(b);
    }

}
