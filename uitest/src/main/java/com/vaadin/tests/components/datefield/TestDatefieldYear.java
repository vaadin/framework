package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;

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
        AbstractDateField df = new TestDateField("Year",
                LocalDate.of(2009, 4, 1));
        df.setLocale(new Locale("en", "US"));
        df.setResolution(Resolution.YEAR);
        df.setResolution(Resolution.MONTH);
        addComponent(df);

    }
}
