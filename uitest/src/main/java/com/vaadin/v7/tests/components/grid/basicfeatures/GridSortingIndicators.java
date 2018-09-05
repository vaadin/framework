package com.vaadin.v7.tests.components.grid.basicfeatures;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.sort.Sort;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.v7.ui.Grid;

public class GridSortingIndicators extends AbstractReindeerTestUI {

    private static int FOO_MIN = 4;
    private static int BAR_MULTIPLIER = 3;
    private static int BAZ_MAX = 132;

    @Override
    protected void setup(VaadinRequest request) {
        final Grid grid = new Grid(createContainer());
        addComponent(grid);
        grid.sort(Sort.by("foo").then("bar", SortDirection.DESCENDING)
                .then("baz"));

        addComponent(new Button("Reverse sorting", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                grid.sort(Sort.by("baz", SortDirection.DESCENDING).then("bar")
                        .then("foo", SortDirection.DESCENDING));
            }
        }));
    }

    private Container.Indexed createContainer() {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("foo", Integer.class, 0);
        container.addContainerProperty("bar", Integer.class, 0);
        container.addContainerProperty("baz", Integer.class, 0);
        for (int i = 0; i < 10; ++i) {
            Item item = container.getItem(container.addItem());
            item.getItemProperty("foo").setValue(FOO_MIN + i);
            item.getItemProperty("baz").setValue(BAZ_MAX - i);
            item.getItemProperty("bar").setValue(BAR_MULTIPLIER * i);
        }
        return container;
    }
}
