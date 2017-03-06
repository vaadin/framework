package com.vaadin.tests.components.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridComponents extends AbstractTestUIWithLog {

    private Map<String, TextField> textFields = new HashMap<>();
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> c = new Grid<String>();
        c.addColumn(t -> new Label(t), new ComponentRenderer());
        c.addColumn(t -> {
            if (textFields.containsKey(t)) {
                log("Reusing old text field for: " + t);
                return textFields.get(t);
            }

            TextField textField = new TextField();
            textField.setValue(t);
            textField.addValueChangeListener(e -> {
                // Value of text field edited by user, store
                textFields.put(t, textField);
            });
            return textField;
        }, new ComponentRenderer());
        c.addColumn(t -> {
            Button button = new Button("Click Me!",
                    e -> Notification.show("Clicked button on row for: " + t,
                            Type.WARNING_MESSAGE));
            button.setId(t.replace(' ', '_').toLowerCase());
            return button;
        }, new ComponentRenderer());

        addComponent(c);
        c.setSizeFull();

        Button resetData = new Button("Reset data", e -> {
            c.setItems(IntStream.range(0, 1000).boxed()
                    .map(i -> "Row " + (i + (counter * 1000))));
            textFields.clear();
            ++counter;
        });
        resetData.click();
        addComponent(resetData);

    }

}
