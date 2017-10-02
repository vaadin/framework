package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.TextField;

public class DateTimeFieldZoneId extends AbstractTestUI {

    static final String ZONE_ID = "zoneId";
    static final String LOCALE_ID = "localeId";
    static final String PATTERN_ID = "patternId";

    static final LocalDateTime INITIAL_DATE_TIME = LocalDateTime.of(2017,
            Month.JANUARY, 1, 0, 0);
    private static final String FORMAT_PATTERN = "dd MMM yyyy - hh:mm:ss a z";

    @Override
    protected String getTestDescription() {
        return "DateTimeField to correctly show time zone name";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8844;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> zoneIdComboBox = new ComboBox<>();
        zoneIdComboBox.setId(ZONE_ID);
        Set<String> zoneIdSet = new TreeSet<>(ZoneId.getAvailableZoneIds());
        zoneIdComboBox.setItems(zoneIdSet);
        addComponent(zoneIdComboBox);

        final ComboBox<Locale> localeIdComboBox = new ComboBox<>();
        localeIdComboBox.setId(LOCALE_ID);
        Stream<Locale> localeStream = Stream.of(Locale.getAvailableLocales())
                .sorted((l1, l2) -> l1.toString().compareTo(l2.toString()));
        localeIdComboBox.setItems(localeStream);
        addComponent(localeIdComboBox);

        final TextField patternTextField = new TextField();
        patternTextField.setId(PATTERN_ID);
        patternTextField.setValue(FORMAT_PATTERN);
        addComponent(patternTextField);

        final DateTimeField dateTimeField = new DateTimeField();
        dateTimeField.setValue(INITIAL_DATE_TIME);
        dateTimeField.setDateFormat(FORMAT_PATTERN);
        addComponent(dateTimeField);

        zoneIdComboBox.addValueChangeListener(event -> {
            String value = event.getValue();
            if (value == null) {
                dateTimeField.setZoneId(null);
            } else {
                dateTimeField.setZoneId(ZoneId.of(value));
            }
        });

        localeIdComboBox.addValueChangeListener(
                event -> dateTimeField.setLocale(event.getValue()));

        patternTextField.addValueChangeListener(
                event -> dateTimeField.setDateFormat(event.getValue()));
    }

}
