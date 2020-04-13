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
    static final String START_YEAR_DATEFIELD_ID = "startYearDateFieldID";
    static final String END_YEAR_DATEFIELD_ID = "endYearDateFieldID";
    static final String FIXED_RANGE_DATEFIELD_ID = "fixedRangeDateFieldID";
    static final String VARIABLE_RANGE_DATEFIELD_ID = "variableRangeDateFieldID";

    static final String INITIAL_ZONE_ID = "CET";
    static final Locale INITIAL_LOCALE = Locale.US;
    static final LocalDateTime INITIAL_DATE_TIME = LocalDateTime.of(LocalDate.now().getYear() + 21, Month.JULY, 1, 0,
	    0);
    static final LocalDate INITIAL_START_DATE = LocalDate.of(INITIAL_DATE_TIME.getYear() - 5, Month.JULY, 1);
    static final LocalDate INITIAL_END_DATE = LocalDate.of(INITIAL_DATE_TIME.getYear() + 5, Month.JULY, 1);
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

	final DateField transitionStartyear = getDateField(START_YEAR_DATEFIELD_ID, INITIAL_START_DATE,
		RANGE_FORMAT_PATTERN, "DST Transitions start year (inclusive):");
	addComponent(transitionStartyear);

	final DateField transitionEndyear = getDateField(END_YEAR_DATEFIELD_ID, INITIAL_END_DATE, RANGE_FORMAT_PATTERN,
		"DST Transitions End year (inclusive):");
	addComponent(transitionEndyear);

	String captionVarField = "A DateTimeField with custom start"
		+ " and end years between which DST zone names are displayed:";
	final DateTimeField dateTimeFieldWithCustomRange = getDateTimeField(VARIABLE_RANGE_DATEFIELD_ID,
		INITIAL_DATE_TIME, TARGET_FORMAT_PATTERN, INITIAL_LOCALE, INITIAL_ZONE_ID, captionVarField);
	dateTimeFieldWithCustomRange.setDaylightSavingTimeRange(INITIAL_START_DATE.getYear(),
		INITIAL_END_DATE.getYear());

	addComponent(dateTimeFieldWithCustomRange);

	transitionStartyear.addValueChangeListener(event -> {
	    int startYear = event.getValue().getYear();
	    int endYear = transitionEndyear.getValue().getYear();
	    if (startYear > endYear) {
		showDateRangeError(transitionStartyear);
	    } else {
		clearErrors(transitionStartyear, transitionEndyear);
		dateTimeFieldWithCustomRange.setDaylightSavingTimeRange(startYear, endYear);
	    }
	});

	transitionEndyear.addValueChangeListener(event -> {
	    int startYear = transitionStartyear.getValue().getYear();
	    int endYear = event.getValue().getYear();
	    if (startYear > endYear) {
		showDateRangeError(transitionEndyear);
	    } else {
		clearErrors(transitionStartyear, transitionEndyear);
		dateTimeFieldWithCustomRange.setDaylightSavingTimeRange(startYear, endYear);
	    }
	});

	String captionFixedField = "A default DateTimeField (By default, "
		+ "DST zones are displayed between 1980 and 20 years into the future):";
	final DateTimeField dateTimeFieldWithDefaultRange = getDateTimeField(FIXED_RANGE_DATEFIELD_ID,
		INITIAL_DATE_TIME, TARGET_FORMAT_PATTERN, INITIAL_LOCALE, INITIAL_ZONE_ID, captionFixedField);
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

    private DateTimeField getDateTimeField(String id, LocalDateTime initialDateTime, String dateFormat, Locale locale,
	    String zoneId, String caption) {
	final DateTimeField dateTimeField = new DateTimeField();
	dateTimeField.setId(id);
	dateTimeField.setValue(initialDateTime);
	dateTimeField.setDateFormat(dateFormat);
	dateTimeField.setLocale(locale);
	dateTimeField.setZoneId(ZoneId.of(zoneId));
	dateTimeField.setCaption(caption);
	return dateTimeField;
    }

    private DateField getDateField(String id, LocalDate initialDate, String dateFormat, String caption) {
	final DateField dateField = new DateField();
	dateField.setId(id);
	dateField.setDateFormat(dateFormat);
	dateField.setCaption(caption);
	dateField.setValue(initialDate);
	return dateField;
    }

    private void clearErrors(DateField transitionStartyear, DateField transitionEndyear) {
	transitionStartyear.setComponentError(null);
	transitionEndyear.setComponentError(null);
    }

    private void showDateRangeError(DateField dateField) {
	dateField.setComponentError(new UserError("Start year must be less than or equal to end year!"));
    }

    private ComboBox<Locale> getLocaleIdComboBox() {
	final ComboBox<Locale> localeIdComboBox = new ComboBox<>();
	localeIdComboBox.setId(LOCALE_ID);
	final Stream<Locale> localeStream = Stream.of(Locale.getAvailableLocales())
		.sorted((l1, l2) -> l1.toString().compareTo(l2.toString()));
	localeIdComboBox.setItems(localeStream);
	localeIdComboBox.setValue(INITIAL_LOCALE);
	localeIdComboBox.setCaption("Locale:");
	return localeIdComboBox;
    }

    private ComboBox<String> getZoneIdComboBox() {
	final ComboBox<String> zoneIdComboBox = new ComboBox<>();
	zoneIdComboBox.setId(ZONE_ID);
	final Set<String> zoneIdSet = new TreeSet<>(ZoneId.getAvailableZoneIds());
	zoneIdComboBox.setItems(zoneIdSet);
	zoneIdComboBox.setValue(INITIAL_ZONE_ID);
	zoneIdComboBox.setCaption("Zone:");
	return zoneIdComboBox;
    }
}
