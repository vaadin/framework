package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

@SuppressWarnings("serial")
public class LowResolution extends TestBase {

    @Override
    protected void setup() {
        final DateField dateField = new DateField();
        dateField.setValue(LocalDate.now());
        dateField.setResolution(Resolution.MONTH);
        dateField.addValueChangeListener(event -> getMainWindow()
                .showNotification("Date now" + event.getValue()));
        getLayout().addComponent(dateField);

        final DateField dateField3 = new DateField();
        // dateField.setValue(new java.util.Date());
        dateField3.setResolution(Resolution.YEAR);
        dateField3.addValueChangeListener(event -> getMainWindow()
                .showNotification("Date now" + event.getValue()));
        getLayout().addComponent(dateField3);

        final InlineDateField dateField2 = new InlineDateField();
        dateField2.setValue(LocalDate.now());
        dateField2.setResolution(Resolution.MONTH);
        dateField2.addValueChangeListener(event -> getMainWindow()
                .showNotification("Date now" + event.getValue()));
        getLayout().addComponent(dateField2);

        getLayout().addComponent(new Button("sync"));

    }

    @Override
    protected String getDescription() {
        return "Date field should work and update its value to the server.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5567;
    }

}
