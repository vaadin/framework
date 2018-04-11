package com.vaadin.tests.components.combobox;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

/**
 * @author Vaadin Ltd
 *
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxEmptyCaption extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> combo = new ComboBox<>();
        combo.setItems(
                IntStream.range(1, 100).mapToObj(number -> "item" + number)
                        .collect(Collectors.toList()));
        addComponent(combo);
        Button setCaption = new Button("Set empty selection caption to 'empty'",
                event -> combo.setEmptySelectionCaption("empty"));
        Button resetCaption = new Button(
                "Set empty selection caption to empty string",
                event -> combo.setEmptySelectionCaption(""));
        Button disableCaption = new Button("Disable empty selection caption",
                event -> combo.setEmptySelectionAllowed(false));
        addComponents(setCaption, resetCaption, disableCaption);
    }
}
