package com.vaadin.tests.elements.grid;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.HeaderRow;

public class GridUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        int rowCount = 100;
        if (request.getParameter("rowCount") != null) {
            rowCount = Integer.parseInt(request.getParameter("rowCount"));
        }

        final Grid<Item> grid = new Grid<Item>();
        grid.setItems(getMockData(rowCount));
        Grid.Column<Item, String> column = grid.addColumn(Item::getFoo)
                .setCaption("foo");
        HeaderRow row = grid.addHeaderRowAt(1);
        row.getCell(column).setText("extra row");
        grid.addColumn(Item::getBar).setCaption("bar");

        grid.setDetailsGenerator(item -> {
            return new Label(
                    "Foo = " + item.getFoo() + " Bar = " + item.getBar());
        });
        grid.addItemClickListener(event -> {
            if (event.getMouseEventDetails().isDoubleClick()) {
                grid.setDetailsVisible(event.getItem(),
                        !grid.isDetailsVisible(event.getItem()));
            }
        });

        addComponent(grid);
    }

    private Collection<Item> getMockData(int rowCount) {
        Collection<Item> data = new ArrayList<Item>();
        for (int i = 0; i < rowCount; i++) {
            Item item = new Item("foo " + i, "bar " + i);
            data.add(item);
        }
        return data;
    }

    @Override
    protected String getTestDescription() {
        return "Test UI for Grid element API";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

    private static class Item {
        private String foo;
        private String bar;

        public Item(String foo, String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }
    }
}
