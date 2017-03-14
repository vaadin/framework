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

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        Column<String, String> column = grid.addColumn(Object::toString)
                .setCaption("To String");

        addComponent(grid);
        addComponent(new Button("Switch renderer", e -> {
            if (textRenderer) {
                ButtonRenderer<String> renderer = new ButtonRenderer<>();
                renderer.addClickListener(event -> Notification
                        .show("Click on row: " + event.getItem()));
                column.setRenderer(renderer);
            } else {
                column.setRenderer(new TextRenderer());
            }
            textRenderer = !textRenderer;
        }));

        grid.setItems(IntStream.range(0, 10).boxed().map(i -> "Foo " + i));
    }

}
