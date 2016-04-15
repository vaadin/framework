package com.vaadin.tests.components.datefield;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.Label;

public class DateFieldMinResolution extends TestBase {

    @Override
    protected void setup() {

        final SimpleDateFormat dformat = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm");

        Calendar cal = Calendar.getInstance();
        cal.set(2019, 1, 1, 1, 1);

        DateField df = new DateField("foo");
        df.setResolution(DateField.RESOLUTION_MIN);
        df.setDateFormat(dformat.toPattern());
        df.setValue(cal.getTime());
        df.setImmediate(true);

        addComponent(df);

        final Label lbl = new Label(dformat.format(cal.getTime()));
        lbl.setCaption("Selected date");

        InlineDateField idf = new InlineDateField("bar");
        idf.setResolution(DateField.RESOLUTION_MIN);
        idf.setDateFormat(dformat.toPattern());
        idf.setValue(cal.getTime());
        idf.setImmediate(true);

        idf.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                lbl.setValue(dformat.format(event.getProperty().getValue()));
            }
        });

        addComponent(idf);
        addComponent(lbl);
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
