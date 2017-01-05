package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.VerticalLayout;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class ComboBoxParentDisable extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final FormLayout formLayout = new FormLayout();

        final ComboBox<String> combo = new ComboBox<>("Item:");
        combo.setItems("Item 1", "Item 2", "Item 3", "Item 4");
        combo.addValueChangeListener(
                event -> log.log("you made a selection change"));

        Button btn1 = new Button("Click me");
        btn1.addClickListener(event -> log.log("you clicked me"));

        formLayout.addComponent(combo);
        formLayout.addComponent(btn1);

        layout.addComponent(formLayout);

        Button btn = new Button("Enable/Disable combobox",
                event -> combo.setEnabled(!combo.isEnabled()));
        layout.addComponent(btn);
        btn = new Button("Enable/Disable parent",
                event -> formLayout.setEnabled(!formLayout.isEnabled()));
        layout.addComponent(btn);

    }

    @Override
    protected String getTestDescription() {
        return "Test for ensuring that disabling a parent properly disables the combobox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10734;
    }
}
