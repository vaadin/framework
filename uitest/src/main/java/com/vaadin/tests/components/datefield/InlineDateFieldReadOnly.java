package com.vaadin.tests.components.datefield;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.InlineDateTimeField;

import java.time.LocalDateTime;
import java.util.Locale;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class InlineDateFieldReadOnly extends AbstractTestUI {
    public static final int SEC = 33;
    public static final int MIN = 15;
    public static final int HOUR = 14;

    @Override
    protected void setup(VaadinRequest request) {
        final InlineDateTimeField timeField = new InlineDateTimeField(
                "A read-only datefield");
        timeField.setResolution(DateTimeResolution.SECOND);
        timeField.setLocale(new Locale("fi"));
        timeField.setId("dF");
        // Set date so that test always has same time
        timeField.setValue(LocalDateTime.of(2018, 9, 15, HOUR, MIN, SEC));
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
