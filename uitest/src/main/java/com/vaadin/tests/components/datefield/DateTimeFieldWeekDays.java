package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;

public class DateTimeFieldWeekDays extends AbstractTestUI {

    private static final Locale localeFI = new Locale("fi", "FI");

    @Override
    protected void setup(VaadinRequest request) {

        DateTimeField dateTimeField = new DateTimeField();
        dateTimeField.setValue(LocalDateTime.of(1999, 12, 1, 12, 00));
        dateTimeField.setShowISOWeekNumbers(true);
        dateTimeField.setLocale(localeFI);

        CheckBox weekNumbersToggle = new CheckBox("Toggle week numbers",
                dateTimeField.isShowISOWeekNumbers());
        weekNumbersToggle.addValueChangeListener(
                event -> dateTimeField.setShowISOWeekNumbers(event.getValue()));

        Button toEnglish = new Button("Change locale",
                click -> dateTimeField.setLocale(Locale.ENGLISH));
        toEnglish.setId("english");
        Button toFinnish = new Button("Change locale",
                click -> dateTimeField.setLocale(localeFI));
        toFinnish.setId("finnish");

        addComponent(dateTimeField);
        addComponent(weekNumbersToggle);
        addComponent(new HorizontalLayout(toEnglish, toFinnish));
    }
}
