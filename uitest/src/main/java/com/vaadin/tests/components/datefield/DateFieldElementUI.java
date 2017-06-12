package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Locale;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUIWithLog;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldElementUI extends AbstractReindeerTestUIWithLog {
    public static final LocalDate TEST_DATE_TIME = DateTimeFieldElementUI.TEST_DATE_TIME
            .toLocalDate();
    public static final LocalDate ANOTHER_TEST_DATE_TIME = DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME
            .toLocalDate();

    @Override
    protected void setup(VaadinRequest request) {
        log.setNumberLogRows(false);
        DateField df = new DateField();
        df.addValueChangeListener(event -> {
            log("Default date field value set to " + event.getValue());
        });
        addComponent(df);
        InlineDateField inlineDateField = new InlineDateField();
        inlineDateField.addValueChangeListener(event -> {
            log("Default inline date field value set to " + event.getValue());
        });
        addComponent(inlineDateField);

        DateField finnishDatefield = new DateField("Finnish");
        finnishDatefield.setId("fi");
        finnishDatefield.setLocale(new Locale("fi", "FI"));
        finnishDatefield.setValue(TEST_DATE_TIME);
        finnishDatefield.addValueChangeListener(event -> {
            log("Finnish date field value set to " + event.getValue());
        });
        addComponent(finnishDatefield);
        DateField usDatefield = new DateField("US");
        usDatefield.setId("us");
        usDatefield.setLocale(Locale.US);
        usDatefield.setValue(TEST_DATE_TIME);
        usDatefield.addValueChangeListener(event -> {
            log("US date field value set to " + event.getValue());
        });
        addComponent(usDatefield);
    }

    @Override
    protected Integer getTicketNumber() {
        return 17090;
    }

    @Override
    protected String getTestDescription() {
        return "DateFieldElement should be accessible using TB4 DateFieldElement.";
    }
}
