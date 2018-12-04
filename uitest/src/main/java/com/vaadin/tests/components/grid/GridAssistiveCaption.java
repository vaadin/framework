package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

public class GridAssistiveCaption extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity());
        grid.setItems("a", "b");
        addComponent(grid);

        addComponent(new Button("addAssistiveCaption", event -> {
            grid.getColumns().get(0)
                    .setAssistiveCaption("Press Enter to sort.");
        }));
        addComponent(new Button("removeAssistiveCaption", event -> {
            grid.getColumns().get(0).setAssistiveCaption(null);
        }));
    }
}
