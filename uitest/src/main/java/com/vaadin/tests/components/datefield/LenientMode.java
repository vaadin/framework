package com.vaadin.tests.components.datefield;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;

public class LenientMode extends TestBase implements ValueChangeListener {

    private static final long serialVersionUID = -9064553409580072387L;

    @Override
    protected String getDescription() {
        return "In lenien mode DateField should accept date input from user like '32/12/09'. In normal mode, an exception should be thrown. ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3175;
    }

    @Override
    protected void setup() {

        @SuppressWarnings("deprecation")
        Date d = new Date(2009 - 1900, 12 - 1, 31, 23, 59, 59);

        LegacyDateField df = new LegacyDateField("Lenient ");
        df.setLocale(new Locale("fi"));
        df.setResolution(LegacyDateField.RESOLUTION_DAY);
        df.setLenient(true);
        df.setImmediate(true);
        df.setValue(d);

        LegacyDateField df2 = new LegacyDateField("Normal ");
        df2.setLocale(new Locale("fi"));
        df2.setResolution(LegacyDateField.RESOLUTION_DAY);
        // df2.setLenient(false);
        df2.setValue(null);
        df2.setImmediate(true);
        df2.setValue(d);

        addComponent(df);
        addComponent(df2);

        df.addListener(this);
        df2.addListener(this);

        df = new LegacyDateField("Lenient with time");
        df.setLocale(new Locale("fi"));
        df.setLenient(true);
        df.setImmediate(true);
        df.setValue(d);

        df2 = new LegacyDateField("Normal with time");
        df2.setLocale(new Locale("fi"));
        // df2.setLenient(false);
        df2.setValue(null);
        df2.setImmediate(true);
        df2.setValue(d);

        addComponent(df);
        addComponent(df2);

        df.addListener(this);
        df2.addListener(this);

        addComponent(new Button("Visit server"));

    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        getMainWindow().showNotification(
                "New value" + event.getProperty().getValue());

    }
}
