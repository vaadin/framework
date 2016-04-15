package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

        final ComboBox combo = new ComboBox("Item:");
        combo.addItem("Item 1");
        combo.addItem("Item 2");
        combo.addItem("Item 3");
        combo.addItem("Item 4");
        combo.addValueChangeListener(new MyValueChangeListener());
        combo.setImmediate(true);

        Button btn1 = new Button("Click me");
        btn1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log.log("you clicked me");
            }
        });

        formLayout.addComponent(combo);
        formLayout.addComponent(btn1);

        layout.addComponent(formLayout);

        Button btn = new Button("Enable/Disable combobox",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        combo.setEnabled(!combo.isEnabled());
                    }
                });
        layout.addComponent(btn);
        btn = new Button("Enable/Disable parent", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                formLayout.setEnabled(!formLayout.isEnabled());
            }
        });
        layout.addComponent(btn);

    }

    private class MyValueChangeListener implements Property.ValueChangeListener {
        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            log.log("you made a selection change");
        }
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
