package com.vaadin.tests.elements.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * UI used to validate ComboBox.selectByText(String s) works properly if input
 * String s contains parentheses
 */
@SuppressWarnings("serial")
public class SelectByText extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        addComponent(layout);

        ComboBox<String> combobox = new ComboBox<>();
        List<String> options = new ArrayList<String>();

        options.add("Value 1");
        options.add("(");
        options.add("(Value");
        options.add("Value 222");
        options.add("Value 22");
        options.add("Value 2");
        options.add("Value(");
        options.add("Value(i)");
        options.add("((Test ) selectByTest() method(with' parentheses)((");
        options.add("Value 3");

        combobox.setItems(options);

        layout.addComponent(combobox);
        combobox.addValueChangeListener(event -> layout.addComponent(
                new Label("Value is now '" + event.getValue() + "'")));
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox's selectByText(String text) method should work if text contains parentheses";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14048;
    }

}
