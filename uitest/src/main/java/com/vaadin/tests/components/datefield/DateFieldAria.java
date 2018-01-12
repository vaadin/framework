package com.vaadin.tests.components.datefield;

import java.time.LocalDate;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldAria extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField = new DateField("Accessible DateField");
        dateField.setValue(LocalDate.now());

        InlineDateField inlineDateField = new InlineDateField(
                "Accessible InlineDateField");
        inlineDateField.setValue(LocalDate.now());

        addComponent(dateField);
        addComponent(inlineDateField);
    }
}
