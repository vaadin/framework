package com.vaadin.tests.data;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Binder;
import com.vaadin.data.Result;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import java.time.LocalDate;

/**
 * Created by elmot on 7/11/2017.
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateValidationUI extends AbstractTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();

        DateField dateField = new DateField("Date") {
            @Override
            protected Result<LocalDate> handleUnparsableDateString(String dateString) {
                if (dateString.equalsIgnoreCase("Y2K")) {
                    return Result.ok(LocalDate.of(2000,1,1));
                } else {
                    return super.handleUnparsableDateString(dateString);
                }
            };
        };
        dateField.setParseErrorMessage("Parse error");
        dateField.setDateOutOfRangeMessage("Out of range");
        layout.addComponent(dateField);
        dateField.addValueChangeListener(event -> {
            System.out.println(dateField.getValue());
        });
        Label errorLabel = new Label();
        layout.addComponent(errorLabel);

        Binder<Void> binder = new Binder<>();
        binder.forField(dateField).withStatusLabel(errorLabel).bind(o -> dateField.getEmptyValue(), null);

        Button button = new Button("Validate!");
        button.addClickListener(event1 -> {
            if (binder.validate().isOk()) {
                System.out.println("Correct");
            } else {
                System.out.println(dateField.isEmpty() + "Error!");
            }
        });
        layout.addComponent(button);

        addComponent(layout);
    }
}
