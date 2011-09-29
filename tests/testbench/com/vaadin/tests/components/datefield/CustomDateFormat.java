package com.vaadin.tests.components.datefield;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.DateField;

public class CustomDateFormat extends TestBase {

    @Override
    protected void setup() {

        Locale locale = new Locale("fi", "FI");
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 0, 1);

        DateField df = new DateField();
        df.setResolution(DateField.RESOLUTION_DAY);
        df.setLocale(locale);
        df.setWidth("300px");

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG, locale);

        if (dateFormat instanceof SimpleDateFormat) {
            String pattern = ((SimpleDateFormat) dateFormat).toPattern();
            df.setDateFormat(pattern);
        }

        df.setValue(cal.getTime());

        addComponent(df);

    }

    @Override
    protected String getDescription() {
        return "Month name should be visible in text box if format pattern includes it";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3490;
    }

}
