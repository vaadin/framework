package com.vaadin.tests.components.datefield;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;

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

        DateField df = new DateField("Lenient ");
        df.setLocale(new Locale("fi"));
        df.setResolution(DateField.RESOLUTION_DAY);
        df.setLenient(true);
        df.setImmediate(true);
        df.setValue(d);

        DateField df2 = new DateField("Normal ");
        df2.setLocale(new Locale("fi"));
        df2.setResolution(DateField.RESOLUTION_DAY);
        // df2.setLenient(false);
        df2.setValue(null);
        df2.setImmediate(true);
        df2.setValue(d);

        addComponent(df);
        addComponent(df2);

        df.addListener(this);
        df2.addListener(this);

        df = new DateField("Lenient with time");
        df.setLocale(new Locale("fi"));
        df.setLenient(true);
        df.setImmediate(true);
        df.setValue(d);

        df2 = new DateField("Normal with time");
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
