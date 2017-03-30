package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Notification;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.TextRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridRendererSwitch extends AbstractTestUI {

    private boolean textRenderer = true;
    private boolean reverse = false;

    @Override
    protected void setup(VaadinRequest request) {
        Grid<Integer> grid = new Grid<>();
        Column<Integer, String> column = grid.addColumn(i -> "Foo " + i)
                .setCaption("Foo").setHidable(true);
        Column<Integer, String> secondColumn = grid.addColumn(i -> "Bar " + i)
                .setCaption("Bar").setHidable(true);

        addComponent(grid);
        addComponent(new Button("Switch", e -> {
            if (textRenderer) {
                ButtonRenderer<Integer> renderer = new ButtonRenderer<>();
                renderer.addClickListener(event -> Notification
                        .show("Click on row: " + event.getItem()));
                column.setRenderer(renderer);
            } else {
                column.setRenderer(new TextRenderer());
            }
            textRenderer = !textRenderer;
        }));
        addComponent(new Button("Reverse", e -> {
            if (reverse) {
                grid.setColumnOrder(column, secondColumn);
            } else {
                grid.setColumnOrder(secondColumn, column);
            }
            reverse = !reverse;
        }));

        grid.setItems(IntStream.range(0, 10).boxed());
    }

}
