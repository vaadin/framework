package com.vaadin.tests.components.combobox;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxValueUpdate extends TestBase {

    @Override
    protected Integer getTicketNumber() {
        return 2451;
    }

    @Override
    protected String getDescription() {
        return "Testcase for ComboBox. Test especially edge values(of page changes) when selecting items with keyboard only.";
    }

    @Override
    protected void setup() {
        ComboBox select = new ComboBox("");
        select.setImmediate(true);
        for (int i = 0; i < 100; i++) {
            select.addItem("item " + i);
        }

        final Label value = new Label();

        select.addListener(new ComboBox.ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                System.err
                        .println("Selected " + event.getProperty().getValue());
                value.setValue("Selected " + event.getProperty().getValue());

            }
        });

        getLayout().addComponent(select);
        getLayout().addComponent(value);

    }

}
