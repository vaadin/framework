package com.vaadin.tests.components.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
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
        Grid<String> grid = new Grid<>();
        grid.addColumn(string -> new Label(string), new ComponentRenderer());
        grid.addComponentColumn(string -> {
            if (textFields.containsKey(string)) {
                log("Reusing old text field for: " + string);
                return textFields.get(string);
            }

            TextField textField = new TextField();
            textField.setValue(string);
            // Make sure all changes are sent immediately
            textField.setValueChangeMode(ValueChangeMode.EAGER);
            textField.addValueChangeListener(e -> {
                // Value of text field edited by user, store
                textFields.put(string, textField);
            });
            return textField;
        });
        grid.addColumn(string -> {
            Button button = new Button("Click Me!",
                    e -> Notification.show(
                            "Clicked button on row for: " + string,
                            Type.WARNING_MESSAGE));
            button.setId(string.replace(' ', '_').toLowerCase());
            return button;
        }, new ComponentRenderer());
        // make sure the buttons and focus outlines fit completely in a row
        grid.setRowHeight(40);

        addComponent(grid);
        grid.setSizeFull();

        Button resetData = new Button("Reset data", e -> {
            grid.setItems(IntStream.range(0, 1000).boxed()
                    .map(i -> "Row " + (i + (counter * 1000))));
            textFields.clear();
            ++counter;
        });
        resetData.click();
        addComponent(resetData);

    }

}
