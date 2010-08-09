package com.vaadin.tests.components.datefield;

import java.util.Date;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

public class DateFieldMinResolution extends TestBase {

    @Override
    protected void setup() {

        DateField df = new DateField("foo");
        df.setResolution(DateField.RESOLUTION_MIN);
        df.setDateFormat("dd/MM/YYY HH:mm");
        df.setValue(new Date());
        df.setImmediate(true);

        df.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                getMainWindow()
                        .showNotification(event.getProperty().toString());
            }
        });

        addComponent(df);

        InlineDateField idf = new InlineDateField("bar");
        idf.setResolution(DateField.RESOLUTION_MIN);
        idf.setDateFormat("dd/MM/YYY HH:mm");
        idf.setValue(new Date());
        idf.setImmediate(true);

        idf.addListener(new Property.ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                getMainWindow()
                        .showNotification(event.getProperty().toString());
            }
        });

        addComponent(idf);
    }

    @Override
    protected String getDescription() {
        return "When the time controls are visible the time should be directed directly to the textfield";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5387;
    }

}
