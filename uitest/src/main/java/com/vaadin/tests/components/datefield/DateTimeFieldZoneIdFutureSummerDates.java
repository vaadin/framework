package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.vaadin.server.UserError;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DateTimeField;

public class DateTimeFieldZoneIdFutureSummerDates extends AbstractTestUI {

    static final String ZONE_ID = "zoneId";
    static final String LOCALE_ID = "localeId";
    static final String START_YEAR_ID = "startyearID";
    static final String END_YEAR_ID = "endyearID";

    static final String INITIAL_ZONE_ID = "Europe/Helsinki";
    static final Locale INITIAL_LOCALE = Locale.US;
    static final LocalDateTime INITIAL_DATE_TIME = LocalDateTime
            .of(LocalDate.now().getYear() + 21, Month.JULY, 1, 0, 0);
    static final LocalDate INITIAL_START_DATE = LocalDate
            .of(INITIAL_DATE_TIME.getYear() - 5, Month.JULY, 1);
    static final LocalDate INITIAL_END_DATE = LocalDate
            .of(INITIAL_DATE_TIME.getYear() + 5, Month.JULY, 1);
    private static final String TARGET_FORMAT_PATTERN = "dd MMM yyyy - z";
    private static final String RANGE_FORMAT_PATTERN = "yyyy";

    @Override
    protected String getTestDescription() {
        return "DateTimeField should correctly show the daylight saving (summer time) zone name "
                + "of a date that occurs within a user-defined range";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11919;
    }

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> zoneIdComboBox = getZoneIdComboBox();
        addComponent(zoneIdComboBox);

        final ComboBox<Locale> localeIdComboBox = getLocaleIdComboBox();
        addComponent(localeIdComboBox);

        final DateField transitionStartyear = getTransitionStartyear();
        addComponent(transitionStartyear);

        final DateField transitionEndyear = getTransitionEndyear();
        addComponent(transitionEndyear);

        final DateTimeField dateTimeFieldWithCustomRange = getDateTimeField();
        dateTimeFieldWithCustomRange.setDaylightSavingTimeRange(
                INITIAL_START_DATE.getYear(), INITIAL_END_DATE.getYear());
        dateTimeFieldWithCustomRange
                .setCaption("A DateTimeField with custom start"
                        + " and end years between which DST zone names are displayed:");
        addComponent(dateTimeFieldWithCustomRange);

        transitionStartyear.addValueChangeListener(event -> {
            int startYear = event.getValue().getYear();
            int endYear = transitionEndyear.getValue().getYear();
            if (startYear > endYear) {
                showDateRangeError(transitionStartyear);
            } else {
                clearErrors(transitionStartyear, transitionEndyear);
                dateTimeFieldWithCustomRange
                        .setDaylightSavingTimeRange(startYear, endYear);
            }
        });

        transitionEndyear.addValueChangeListener(event -> {
            int startYear = transitionStartyear.getValue().getYear();
            int endYear = event.getValue().getYear();
            if (startYear > endYear) {
                showDateRangeError(transitionEndyear);
            } else {
                clearErrors(transitionStartyear, transitionEndyear);
                dateTimeFieldWithCustomRange
                        .setDaylightSavingTimeRange(startYear, endYear);
            }
        });

        final DateTimeField dateTimeFieldWithDefaultRange = getDateTimeField();
        dateTimeFieldWithDefaultRange
                .setCaption("A default DateTimeField (By default, "
                        + "DST zones are displayed between 1980 and 20 years into the future):");
        addComponent(dateTimeFieldWithDefaultRange);

        zoneIdComboBox.addValueChangeListener(event -> {
            final String value = event.getValue();
            if (value == null) {
                dateTimeFieldWithCustomRange.setZoneId(null);
                dateTimeFieldWithDefaultRange.setZoneId(null);
            } else {
                dateTimeFieldWithCustomRange.setZoneId(ZoneId.of(value));
                dateTimeFieldWithDefaultRange.setZoneId(ZoneId.of(value));
            }
        });

        localeIdComboBox.addValueChangeListener(event -> {
            dateTimeFieldWithCustomRange.setLocale(event.getValue());
            dateTimeFieldWithDefaultRange.setLocale(event.getValue());
        });
    }

    private DateTimeField getDateTimeField() {
        final DateTimeField dateTimeField = new DateTimeField();
        dateTimeField.setValue(INITIAL_DATE_TIME);
        dateTimeField.setDateFormat(TARGET_FORMAT_PATTERN);
        dateTimeField.setLocale(INITIAL_LOCALE);
        dateTimeField.setZoneId(ZoneId.of(INITIAL_ZONE_ID));
        return dateTimeField;
    }

    private DateField getTransitionStartyear() {
        final DateField transitionStartyear = new DateField();
        transitionStartyear.setId(START_YEAR_ID);
        transitionStartyear.setDateFormat(RANGE_FORMAT_PATTERN);
        transitionStartyear
                .setCaption("DST Transitions start year (inclusive):");
        transitionStartyear.setValue(INITIAL_START_DATE);
        return transitionStartyear;
    }

    private DateField getTransitionEndyear() {
        final DateField transitionEndyear = new DateField();
        transitionEndyear.setId(END_YEAR_ID);
        transitionEndyear.setDateFormat(RANGE_FORMAT_PATTERN);
        transitionEndyear.setCaption("DST Transitions end year (inclusive):");
        transitionEndyear.setValue(INITIAL_END_DATE);
        return transitionEndyear;
    }

    private void clearErrors(DateField transitionStartyear,
            DateField transitionEndyear) {
        transitionStartyear.setComponentError(null);
        transitionEndyear.setComponentError(null);
    }

    private void showDateRangeError(DateField dateField) {
        dateField.setComponentError(new UserError(
                "Start year must be less than or equal end year!"));
    }

    private ComboBox<Locale> getLocaleIdComboBox() {
        final ComboBox<Locale> localeIdComboBox = new ComboBox<>();
        localeIdComboBox.setId(LOCALE_ID);
        final Stream<Locale> localeStream = Stream
                .of(Locale.getAvailableLocales())
                .sorted((l1, l2) -> l1.toString().compareTo(l2.toString()));
        localeIdComboBox.setItems(localeStream);
        localeIdComboBox.setValue(INITIAL_LOCALE);
        localeIdComboBox.setCaption("Locale:");
        return localeIdComboBox;
    }

    private ComboBox<String> getZoneIdComboBox() {
        final ComboBox<String> zoneIdComboBox = new ComboBox<>();
        zoneIdComboBox.setId(ZONE_ID);
        final Set<String> zoneIdSet = new TreeSet<>(
                ZoneId.getAvailableZoneIds());
        zoneIdComboBox.setItems(zoneIdSet);
        zoneIdComboBox.setValue(INITIAL_ZONE_ID);
        zoneIdComboBox.setCaption("Zone:");
        return zoneIdComboBox;
    }
}
