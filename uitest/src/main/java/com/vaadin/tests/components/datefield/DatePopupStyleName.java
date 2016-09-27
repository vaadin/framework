package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractDateField;

public class DatePopupStyleName extends TestBase {
    @Override
    public void setup() {
        setTheme("reindeer-tests");

        final AbstractDateField df = new TestDateField();
        df.setValue(LocalDate.of(1970, 1, 15));
        df.setWidth("200px");
        df.setRequired(true);
        df.setComponentError(new UserError("abc"));
        df.addStyleName("popup-style");
        addComponent(df);
    }

    @Override
    protected String getDescription() {
        return "The DateField is given a style name 'test', but that style isn't applied on the calendar popup element.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8083;
    }

}
