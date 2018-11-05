package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Binder;
import com.vaadin.data.Result;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateTextHandling extends AbstractTestUI {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.UK);

    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();

        DateField dateField = new DateField("Date") {
            @Override
            protected Result<LocalDate> handleUnparsableDateString(
                    String dateString) {
                if (dateString.equalsIgnoreCase("Y2K")) {
                    return Result.ok(LocalDate.of(2000, 1, 1));
                } else {
                    return super.handleUnparsableDateString(dateString);
                }
            }

            ;
        };
        dateField.setParseErrorMessage("Parse error");
        dateField.setDateOutOfRangeMessage("Out of range");
        layout.addComponent(dateField);
        Label errorLabel = new Label();
        errorLabel.setId("errorLabel");
        layout.addComponent(errorLabel);

        Binder<Void> binder = new Binder<>();
        binder.forField(dateField).withStatusLabel(errorLabel)
                .bind(o -> dateField.getValue(), (aVoid, date) -> {
                });

        Button buttonValidate = new Button("Validate!");
        buttonValidate.addClickListener(event1 -> {
            binder.validate();
            if (dateField.getValue() == null) {
                Notification.show("NULL");
            } else {
                Notification
                        .show(DATE_TIME_FORMATTER.format(dateField.getValue()));
            }

        });
        layout.addComponent(buttonValidate);

        Button setValueButton = new Button("Set 2011-12-13",
                event -> dateField.setValue(LocalDate.of(2011, 12, 13)));
        layout.addComponent(setValueButton);
        addComponent(layout);
    }
}
