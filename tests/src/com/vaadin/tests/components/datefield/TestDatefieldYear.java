package com.vaadin.tests.components.datefield;

import java.util.Date;
import java.util.Locale;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.DateField;

public class TestDatefieldYear extends TestBase {

    @Override
    protected String getDescription() {
        return "A popup with resolution year or month should update the textfield when browsing. The value displayed in the textfield should always be the same as the popup shows.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2813;
    }

    @Override
    protected void setup() {
        @SuppressWarnings("deprecation")
        DateField df = new DateField("Year", new Date(2009 - 1900, 4 - 1, 1));
        df.setLocale(new Locale("en", "US"));
        df.setResolution(DateField.RESOLUTION_YEAR);
        df.setResolution(DateField.RESOLUTION_MONTH);
        addComponent(df);

    }
}
