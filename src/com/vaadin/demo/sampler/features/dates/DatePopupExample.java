package com.vaadin.demo.sampler.features.dates;

import java.text.DateFormat;
import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class DatePopupExample extends VerticalLayout implements
        Property.ValueChangeListener {

    private PopupDateField datetime;

    public DatePopupExample() {
        setSpacing(true);

        datetime = new PopupDateField("Please select the starting time:");

        // Set the value of the PopupDateField to current date
        datetime.setValue(new java.util.Date());

        // Set the correct resolution
        datetime.setResolution(PopupDateField.RESOLUTION_DAY);

        // Add valuechangelistener
        datetime.addListener(this);
        datetime.setImmediate(true);

        addComponent(datetime);
    }

    public void valueChange(ValueChangeEvent event) {
        // Get the new value and format it to the current locale
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);
        Object value = event.getProperty().getValue();
        if (value == null || !(value instanceof Date)) {
            getWindow().showNotification("Invalid date entered");
        } else {
            String dateOut = dateFormatter.format(value);
            // Show notification
            getWindow().showNotification("Starting date: " + dateOut);
        }
    }
}
