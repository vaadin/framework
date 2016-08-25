package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

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
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.add("item " + i);
        }
        ComboBox<String> select = new ComboBox<>("", items);

        final Label value = new Label();

        select.addValueChangeListener(event -> {
            System.err.println("Selected " + event.getValue());
            value.setValue("Selected " + event.getValue());
        });

        getLayout().addComponent(select);
        getLayout().addComponent(value);

    }

}
