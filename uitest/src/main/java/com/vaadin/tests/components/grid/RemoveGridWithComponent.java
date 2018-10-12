package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Vaadin Ltd
 *
 */
public class RemoveGridWithComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        addComponent(layout);
        Grid<String> grid = new Grid<>();
        grid.setId("grid-with-component");
        grid.addComponentColumn(text -> new Label(text));
        grid.setItems("item 1", "item 2");
        Button button = new Button("remove grid",
                event -> layout.removeComponent(grid));
        button.setId("remove-grid");
        layout.addComponents(grid, button);
    }
}
