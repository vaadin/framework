package com.vaadin.tests.components.datefield;

import com.vaadin.data.Property.ValueChangeEvent;
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
        dateField.setResolution(PopupDateField.RESOLUTION_MONTH);
        dateField.addListener(new PopupDateField.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getMainWindow().showNotification(
                        "Date now" + event.getProperty());
            }
        });
        dateField.setImmediate(true);
        getLayout().addComponent(dateField);

        final PopupDateField dateField3 = new PopupDateField();
        // dateField.setValue(new java.util.Date());
        dateField3.setResolution(PopupDateField.RESOLUTION_YEAR);
        dateField3.addListener(new PopupDateField.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                getMainWindow().showNotification(
                        "Date now" + event.getProperty());
            }
        });
        dateField3.setImmediate(true);
        getLayout().addComponent(dateField3);

        final InlineDateField dateField2 = new InlineDateField();
        dateField2.setValue(new java.util.Date());
        dateField2.setResolution(PopupDateField.RESOLUTION_MONTH);
        dateField2.addListener(new PopupDateField.ValueChangeListener() {
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
        immediate.addListener(new CheckBox.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean immediate = !dateField.isImmediate();
                dateField.setImmediate(immediate);
                dateField2.setImmediate(immediate);
                dateField3.setImmediate(immediate);
            }
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
