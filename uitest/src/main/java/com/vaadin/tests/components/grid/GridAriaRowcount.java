package com.vaadin.tests.components.grid;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;

/**
 * @author Vaadin Ltd
 *
 */
public class GridAriaRowcount extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(ValueProvider.identity());
        grid.setItems("a", "b");
        addComponent(grid);

        addComponent(new Button("addFooter", event -> grid.addFooterRowAt(0)));
        addComponent(
                new Button("removeFooter", event -> grid.removeFooterRow(0)));

        addComponent(new Button("addHeader", event -> grid.addHeaderRowAt(1)));
        addComponent(
                new Button("removeHeader", event -> grid.removeHeaderRow(1)));

        addComponent(new Button("setItemsTo3",
                event -> grid.setItems("a", "b", "c")));
        addComponent(new Button("setItemsTo6",
                event -> grid.setItems("a", "b", "c", "d", "e", "f")));

        addComponent(new Button("updateAll", event -> {
            grid.addFooterRowAt(0);
            grid.addHeaderRowAt(0);
            grid.setItems("a", "b", "c", "d");
        }));
    }
}
