package com.vaadin.tests.components.datefield;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;

import java.time.LocalDateTime;
import java.util.Locale;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateTimeFieldAfterReadOnly extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final DateTimeField timeField = new DateTimeField(
                "A read-only datefield");
        timeField.setResolution(DateTimeResolution.MINUTE);
        timeField.setLocale(new Locale("fi"));
        timeField.setId("dF");
        // Set date so that testing always has same time
        timeField.setValue(LocalDateTime.now());
        timeField.setReadOnly(true);

        addComponent(timeField);

        Button b = new Button("Switch read-only");
        b.addClickListener(event -> {
            timeField.setReadOnly(!timeField.isReadOnly());
        });
        b.setId("readOnlySwitch");

        addComponent(b);
    }
}
