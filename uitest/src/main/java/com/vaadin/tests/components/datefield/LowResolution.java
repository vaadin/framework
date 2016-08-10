package com.vaadin.tests.components.datefield;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.legacy.ui.LegacyInlineDateField;
import com.vaadin.legacy.ui.LegacyPopupDateField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;

@SuppressWarnings("serial")
public class LowResolution extends TestBase {

    @Override
    protected void setup() {
        final LegacyPopupDateField dateField = new LegacyPopupDateField();
        dateField.setValue(new java.util.Date());
        dateField.setResolution(LegacyPopupDateField.RESOLUTION_MONTH);
        dateField.addListener(new LegacyPopupDateField.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getMainWindow().showNotification(
                        "Date now" + event.getProperty());
            }
        });
        dateField.setImmediate(true);
        getLayout().addComponent(dateField);

        final LegacyPopupDateField dateField3 = new LegacyPopupDateField();
        // dateField.setValue(new java.util.Date());
        dateField3.setResolution(LegacyPopupDateField.RESOLUTION_YEAR);
        dateField3.addListener(new LegacyPopupDateField.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getMainWindow().showNotification(
                        "Date now" + event.getProperty());
            }
        });
        dateField3.setImmediate(true);
        getLayout().addComponent(dateField3);

        final LegacyInlineDateField dateField2 = new LegacyInlineDateField();
        dateField2.setValue(new java.util.Date());
        dateField2.setResolution(LegacyPopupDateField.RESOLUTION_MONTH);
        dateField2.addListener(new LegacyPopupDateField.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getMainWindow().showNotification(
                        "Date now" + event.getProperty());
            }
        });
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
