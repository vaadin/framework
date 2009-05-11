package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxValueUpdate extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2451;
    }

    private Label selectedLabel;

    @Override
    protected String getDescription() {
        return "Testcase for value update for ComboBox. The server-side value should be updated immediately when a new item is selected in the dropdown";
    }

    @Override
    protected void setup() {
        ComboBox combobox = new ComboBox();
        combobox.setImmediate(true);
        combobox.addItem("Item 1");
        combobox.addItem("Item 2");
        combobox.addItem("Item 3");
        combobox.addListener(new ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                Object p = event.getProperty().getValue();
                selectedLabel.setValue("Server side value: " + p);
            }

        });
        selectedLabel = new Label("Server side value: " + combobox.getValue());
        getLayout().addComponent(selectedLabel);

        getLayout().addComponent(combobox);

    }

}
