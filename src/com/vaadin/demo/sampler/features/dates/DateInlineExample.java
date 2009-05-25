package com.vaadin.demo.sampler.features.dates;

import java.text.DateFormat;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DateInlineExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private InlineDateField datetime;

    public DateInlineExample() {
        setSpacing(true);

        datetime = new InlineDateField("Please select the starting time:");

        // Set the value of the PopupDateField to current date
        datetime.setValue(new java.util.Date());

        // Set the correct resolution
        datetime.setResolution(InlineDateField.RESOLUTION_DAY);

        // Add valuechangelistener
        datetime.addListener(this);
        datetime.setImmediate(true);

        addComponent(datetime);
    }

    public void valueChange(ValueChangeEvent event) {
        // Get the new value and format it to the current locale
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
        String dateOut = dateFormatter.format(event.getProperty().getValue());
        // Show notification
        getWindow().showNotification("Starting date: " + dateOut);
    }
}
