package com.vaadin.tests.components.grid;

import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.ComponentRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridComponents extends UI {

    @Override
    public void init(VaadinRequest request) {
        Grid<String> c = new Grid<String>();
        c.addColumn(t -> new Label(t), new ComponentRenderer());
        c.addColumn(t -> {
            Button button = new Button("Click Me!",
                    e -> Notification.show("Clicked button on row for: " + t,
                            Type.WARNING_MESSAGE));
            button.setId(t.replace(' ', '_').toLowerCase());
            return button;
        }, new ComponentRenderer());

        c.setItems(IntStream.range(0, 1000).boxed().map(i -> "Row " + i));

        setContent(c);
        c.setSizeFull();
    }

}
