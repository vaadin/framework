package com.vaadin.tests.components.grid;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.v7.data.Container.Indexed;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;

/**
 * Test UI for Grid initial rendering performance profiling.
 */
@Widgetset("com.vaadin.v7.Vaadin7WidgetSet")
public class GridManyColumnsV7 extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Grid grid = new Grid();
        grid.setSizeFull();
        for (int i = 0; i < 80; i++) {
            grid.addColumn("Column_" + i).setWidth(200);
        }
        grid.setContainerDataSource(createContainer());
        addComponent(grid);
    }

    private Indexed createContainer() {
        Indexed container = new IndexedContainer();

        container.addContainerProperty("foo", String.class, "foo");
        container.addContainerProperty("bar", Integer.class, 0);
        // km contains double values from 0.0 to 2.0
        container.addContainerProperty("km", Double.class, 0);
        for (int i = 0; i < 80; ++i) {
            container.addContainerProperty("Column_" + i, String.class,
                    "novalue");
        }

        for (int i = 0; i <= 10; ++i) {
            Object itemId = container.addItem();
            Item item = container.getItem(itemId);
            for (int j = 0; j < 80; ++j) {
                item.getItemProperty("Column_" + j).setValue("novalue");
            }
        }

        return container;
    }

}
