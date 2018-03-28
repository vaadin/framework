package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateTimeField;

/**
 * @author Vaadin Ltd
 *
 */
public class CustomDateTimeFormat extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(new Locale("fi", "FI"));

        DateTimeField field = new DateTimeField();
        field.setResolution(DateTimeResolution.SECOND);
        field.setWidth("300px");

        String pattern = "d. MMMM'ta 'yyyy 'klo 'H.mm.ss";
        field.setDateFormat(pattern);

        field.setValue(LocalDateTime.of(2010, 1, 1, 12, 23, 45));

        addComponent(field);

    }

    @Override
    protected String getTestDescription() {
        return "Month name should be visible in text box if format pattern includes it";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3490;
    }

}
