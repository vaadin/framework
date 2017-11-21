package com.vaadin.tests.components.grid;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.ComponentRenderer;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class GridComponentsVisibility extends AbstractTestUIWithLog {

    private Map<String, TextField> textFields = new HashMap<>();
    private int counter = 0;

    @Override
    protected void setup(VaadinRequest request) {
        Grid<String> grid = new Grid<>();
        grid.addColumn(string -> new Label(string), new ComponentRenderer())
            .setId("label").setCaption("Column 0");
        grid.getDefaultHeaderRow().getCell("label")
            .setComponent(new Label("Label"));
        grid.addComponentColumn(string -> {
            if (textFields.containsKey(string)) {
                log("Reusing old text field for: " + string);
                return textFields.get(string);
            }
            TextField textField = new TextField();
            textField.setValue(string);
            textField.setWidth("100%");
            textFields.put(string, textField);
            return textField;
        }).setId("textField").setCaption("TextField");
        grid.addColumn(string -> {

            Button button = new Button("Click Me!",
                event -> toggleFieldVisibility(string));
            button.setId(string.replace(' ', '_').toLowerCase(Locale.ROOT));
            return button;
        }, new ComponentRenderer()).setId("button").setCaption("Button");
        // make sure the buttons and focus outlines fit completely in a row
        grid.setRowHeight(40);

        grid.getDefaultHeaderRow().join("textField", "button")
            .setText("Other Components");

        addComponent(grid);
        grid.setSizeFull();

        grid.setItems(IntStream.range(0, 5).boxed()
            .map(i -> "Row " + (i + (counter * 1000))));

    }

    private void toggleFieldVisibility(String string) {
        textFields.get(string).setVisible(!textFields.get(string).isVisible());
    }

}
