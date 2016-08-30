package com.vaadin.tests.components.datefield;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.PopupDateField;

@SuppressWarnings("serial")
public class LowResolution extends TestBase {

    @Override
    protected void setup() {
        final PopupDateField dateField = new PopupDateField();
        dateField.setValue(new java.util.Date());
        dateField.setResolution(Resolution.MONTH);
        dateField.addValueChangeListener(event -> getMainWindow()
                .showNotification("Date now" + event.getValue()));
        dateField.setImmediate(true);
        getLayout().addComponent(dateField);

        final PopupDateField dateField3 = new PopupDateField();
        // dateField.setValue(new java.util.Date());
        dateField3.setResolution(Resolution.YEAR);
        dateField3.addValueChangeListener(event -> getMainWindow()
                .showNotification("Date now" + event.getValue()));
        dateField3.setImmediate(true);
        getLayout().addComponent(dateField3);

        final InlineDateField dateField2 = new InlineDateField();
        dateField2.setValue(new java.util.Date());
        dateField2.setResolution(Resolution.MONTH);
        dateField2.addValueChangeListener(event -> getMainWindow()
                .showNotification("Date now" + event.getValue()));
        dateField2.setImmediate(true);
        getLayout().addComponent(dateField2);

        CheckBox immediate = new CheckBox(
                "Immediate (use sync button to change fields) ");
        immediate.setValue(true);
        immediate.addListener(event -> {
            boolean newImmediate = !dateField.isImmediate();
            dateField.setImmediate(newImmediate);
            dateField2.setImmediate(newImmediate);
            dateField3.setImmediate(newImmediate);
        });

        getLayout().addComponent(immediate);
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
