package com.itmill.toolkit.demo.sampler.features.dates;

import java.text.DateFormat;

import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.ui.InlineDateField;
import com.itmill.toolkit.ui.VerticalLayout;

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
