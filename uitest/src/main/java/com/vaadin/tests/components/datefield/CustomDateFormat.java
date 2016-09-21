package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Locale;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;

public class CustomDateFormat extends TestBase {

    @Override
    protected void setup() {

        Locale locale = new Locale("fi", "FI");
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 0, 1);

        AbstractDateField df = new TestDateField();
        df.setResolution(Resolution.DAY);
        df.setLocale(locale);
        df.setWidth("300px");

        String pattern = "d. MMMM'ta 'yyyy 'klo 'H.mm.ss";
        df.setDateFormat(pattern);

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
