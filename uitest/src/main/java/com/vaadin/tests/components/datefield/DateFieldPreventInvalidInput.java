package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;

public class DateFieldPreventInvalidInput extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField = new DateField();
        dateField.setRangeStart(LocalDate.ofYearDay(2019, 1));
        dateField.setRangeEnd(LocalDate.ofYearDay(2019, 365));
        dateField.setPreventInvalidInput(true);
        Button button = new Button("", event -> {
            dateField.clear();
            dateField.setPreventInvalidInput(false);
        });
        Label value = new Label("no-value");
        dateField.addValueChangeListener(event -> {
            value.setValue(dateField.getValue().toString());
        });
        addComponents(dateField,value,button);
    }

}
