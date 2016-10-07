package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.data.HasValue.ValueChange;
import com.vaadin.data.HasValue.ValueChangeListener;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Button;

public class LenientMode extends TestBase
        implements ValueChangeListener<LocalDate> {

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

        LocalDate d = LocalDate.of(2009, 12, 31);

        AbstractDateField df = new TestDateField("Lenient ");
        df.setLocale(new Locale("fi"));
        df.setResolution(Resolution.DAY);
        df.setLenient(true);
        df.setValue(d);

        AbstractDateField df2 = new TestDateField("Normal ");
        df2.setLocale(new Locale("fi"));
        df2.setResolution(Resolution.DAY);
        // df2.setLenient(false);
        df2.setValue(null);
        df2.setValue(d);

        addComponent(df);
        addComponent(df2);

        df.addValueChangeListener(this);
        df2.addValueChangeListener(this);

        df = new TestDateField("Lenient with time");
        df.setLocale(new Locale("fi"));
        df.setLenient(true);
        df.setValue(d);

        df2 = new TestDateField("Normal with time");
        df2.setLocale(new Locale("fi"));
        // df2.setLenient(false);
        df2.setValue(null);
        df2.setValue(d);

        addComponent(df);
        addComponent(df2);

        df.addValueChangeListener(this);
        df2.addValueChangeListener(this);

        addComponent(new Button("Visit server"));

    }

    @Override
    public void accept(ValueChange<LocalDate> event) {
        getMainWindow().showNotification("New value" + event.getValue());
    }

}
