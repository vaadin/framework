package com.vaadin.tests.components.datefield;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.legacy.ui.LegacyInlineDateField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;

public class DateFieldMinResolution extends TestBase {

    @Override
    protected void setup() {

        final SimpleDateFormat dformat = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm");

        Calendar cal = Calendar.getInstance();
        cal.set(2019, 1, 1, 1, 1);

        LegacyDateField df = new LegacyDateField("foo");
        df.setResolution(LegacyDateField.RESOLUTION_MIN);
        df.setDateFormat(dformat.toPattern());
        df.setValue(cal.getTime());
        df.setImmediate(true);

        addComponent(df);

        final Label lbl = new Label(dformat.format(cal.getTime()));
        lbl.setCaption("Selected date");

        LegacyInlineDateField idf = new LegacyInlineDateField("bar");
        idf.setResolution(LegacyDateField.RESOLUTION_MIN);
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
