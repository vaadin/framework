package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;

public class DateFieldMonthResolutionStatusChange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField = new DateField();
        dateField.setResolution(DateResolution.MONTH);
        dateField.setValue(LocalDate.of(2019, 1, 1));
        dateField.setReadOnly(true);

        Button dateReadOnlySwitch = new Button("Toggle read-only");
        dateReadOnlySwitch.setId("readOnly");
        dateReadOnlySwitch.addClickListener(event -> {
            dateField.setReadOnly(!dateField.isReadOnly());
        });

        Button addRangeButton = new Button("Add range");
        addRangeButton.setId("addRange");
        addRangeButton.addClickListener(event -> {
            dateField.setRangeStart(LocalDate.of(2018, 1, 1));
            dateField.setRangeEnd(LocalDate.of(2020, 1, 1));
        });

        Button resetValueButton = new Button("Reset value");
        resetValueButton.setId("resetValue");
        resetValueButton.addClickListener(event -> {
            dateField.setValue(LocalDate.now());
        });

        addComponent(dateField);
        addComponent(dateReadOnlySwitch);
        addComponent(addRangeButton);
        addComponent(resetValueButton);
    }

    @Override
    protected String getTestDescription() {
        return "Changing any field status (e.g. read-only or range) before "
                + "the DateField popup has been opened should not change "
                + "the date to current.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11864;
    }
}
