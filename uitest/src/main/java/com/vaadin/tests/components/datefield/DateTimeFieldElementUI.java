package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.util.Locale;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.InlineDateTimeField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateTimeFieldElementUI extends AbstractReindeerTestUIWithLog {
    public static final LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2017,
            12, 13, 17, 58);
    public static final LocalDateTime ANOTHER_TEST_DATE_TIME = LocalDateTime
            .of(2016, 11, 12, 16, 57);

    @Override
    protected void setup(VaadinRequest request) {
        log.setNumberLogRows(false);
        DateTimeField df = new DateTimeField();
        df.addValueChangeListener(event -> {
            log("Default date field value set to " + event.getValue());
        });
        addComponent(df);
        InlineDateTimeField inlineDateTimeField = new InlineDateTimeField();
        inlineDateTimeField.addValueChangeListener(event -> {
            log("Default inline date field value set to " + event.getValue());
        });
        addComponent(inlineDateTimeField);

        DateTimeField finnishDateTimeField = new DateTimeField("Finnish");
        finnishDateTimeField.setId("fi");
        finnishDateTimeField.setLocale(new Locale("fi", "FI"));
        finnishDateTimeField.setValue(TEST_DATE_TIME);
        finnishDateTimeField.addValueChangeListener(event -> {
            log("Finnish date field value set to " + event.getValue());
        });
        addComponent(finnishDateTimeField);
        DateTimeField usDateTimeField = new DateTimeField("US");
        usDateTimeField.setId("us");
        usDateTimeField.setLocale(Locale.US);
        usDateTimeField.setValue(TEST_DATE_TIME);
        usDateTimeField.addValueChangeListener(event -> {
            log("US date field value set to " + event.getValue());
        });
        addComponent(usDateTimeField);
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
