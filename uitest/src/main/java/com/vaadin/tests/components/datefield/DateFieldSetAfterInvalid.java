package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;

public class DateFieldSetAfterInvalid extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "DateField to programatically change the value after having invalid text";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9763;
    }

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField = new DateField();
        addComponent(dateField);

        Button nowButton = new Button("Today");
        nowButton
                .addClickListener(event -> dateField.setValue(LocalDate.now()));
        addComponent(nowButton);

        Button clearButton = new Button("Clear");
        clearButton.addClickListener(event -> dateField.clear());
        addComponent(clearButton);
    }

}
