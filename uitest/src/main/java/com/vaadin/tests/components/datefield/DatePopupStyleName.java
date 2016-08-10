package com.vaadin.tests.components.datefield;

import java.util.Date;

import com.vaadin.legacy.ui.LegacyDateField;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;

public class DatePopupStyleName extends TestBase {
    @Override
    public void setup() {
        setTheme("reindeer-tests");

        final LegacyDateField df = new LegacyDateField();
        df.setValue(new Date(1203910239L));
        df.setResolution(LegacyDateField.RESOLUTION_SEC);
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
