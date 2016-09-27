package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;

public class CustomDateFormat extends TestBase {

    @Override
    protected void setup() {
        Locale locale = new Locale("fi", "FI");
        AbstractDateField df = new TestDateField();
        df.setResolution(Resolution.DAY);
        df.setLocale(locale);
        df.setWidth("300px");

        String pattern = "d. MMMM'ta 'yyyy 'klo";
        df.setDateFormat(pattern);

        df.setValue(LocalDate.of(2010, 1, 1));

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
