package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.HtmlRenderer;

public class GridColumnAutoWidth extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        Grid<Object> grid = new Grid<>();
        grid.addColumn(item -> "<span>equal width</span>", new HtmlRenderer())
                .setId("equal width");
        grid.addColumn(item -> "<span>a very long cell content</span>",
                new HtmlRenderer()).setId("short");
        grid.addColumn(item -> "<span>short</span>", new HtmlRenderer())
                .setId("a very long header content");

        grid.addColumn(item -> "<span>fixed width narrow</span>",
                new HtmlRenderer()).setId("fixed width narrow").setWidth(50);
        grid.addColumn(item -> "<span>fixed width wide</span>",
                new HtmlRenderer()).setId("fixed width wide").setWidth(200);

        for (Column<Object, ?> column : grid.getColumns()) {
            column.setExpandRatio(0);
            grid.getHeaderRow(0).getCell(column)
                    .setHtml("<span>" + column.getId() + "</span>");
        }

        grid.setItems(new Object());

        grid.setSelectionMode(SelectionMode.NONE);
        grid.setWidth("750px");
        addComponent(grid);
    }

}
