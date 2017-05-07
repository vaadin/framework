package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.util.Locale;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.InlineDateTimeField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateTimeFieldElementUI extends AbstractReindeerTestUI {
    public static final LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2017,
            12, 13, 17, 58);
    public static final LocalDateTime ANOTHER_TEST_DATE_TIME = LocalDateTime
            .of(2016, 11, 12, 16, 57);

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new DateTimeField());
        addComponent(new InlineDateTimeField());

        DateTimeField finnishDatefield = new DateTimeField("Finnish");
        finnishDatefield.setId("fi");
        finnishDatefield.setLocale(new Locale("fi", "FI"));
        finnishDatefield.setValue(TEST_DATE_TIME);
        addComponent(finnishDatefield);
        DateTimeField usDatefield = new DateTimeField("US");
        usDatefield.setId("us");
        usDatefield.setLocale(Locale.US);
        usDatefield.setValue(TEST_DATE_TIME);
        addComponent(usDatefield);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17090;
    }

    @Override
    protected String getTestDescription() {
        return "DateTimeField should be accessible using TB4 DateTimeFieldElement.";
    }
}
