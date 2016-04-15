package com.vaadin.tests.components.datefield;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;

public class DateFieldTimezone extends TestBase {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Locale EN = Locale.ENGLISH;
    private final Log log = new Log(5);
    private final DateField dateField = new DateField();
    private static final String nullValue = "";

    @Override
    protected void setup() {
        dateField.setResolution(Resolution.SECOND);

        ArrayList<String> timeZoneCodes = new ArrayList<String>();
        timeZoneCodes.add(nullValue);
        timeZoneCodes.addAll(Arrays.asList(TimeZone.getAvailableIDs()));
        ComboBox timezoneSelector = new ComboBox("Select time zone",
                timeZoneCodes) {
            @Override
            public String getItemCaption(Object itemId) {
                if (itemId == nullValue || itemId == null) {
                    TimeZone timeZone = TimeZone.getDefault();
                    return "Default time zone (" + timeZone.getDisplayName()
                            + ")";
                } else {
                    TimeZone timeZone = TimeZone.getTimeZone((String) itemId);
                    return itemId + " (" + timeZone.getDisplayName() + ")";
                }
            }
        };
        timezoneSelector.setValue("UTC");
        timezoneSelector.setImmediate(true);
        timezoneSelector.setNullSelectionAllowed(true);
        timezoneSelector.setNullSelectionItemId(nullValue);
        timezoneSelector.setFilteringMode(FilteringMode.CONTAINS);
        timezoneSelector.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Object value = event.getProperty().getValue();
                TimeZone timeZone;
                if (value == nullValue || value == null) {
                    timeZone = null;
                    log.log("Change to default time zone "
                            + TimeZone.getDefault().getID());
                } else {
                    timeZone = TimeZone.getTimeZone((String) value);
                    log.log("Changed to time zone " + timeZone.getID());
                }
                dateField.setTimeZone(timeZone);
            }
        });

        Calendar cal = Calendar.getInstance(UTC);
        cal.set(2010, 0, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        dateField.setValue(cal.getTime());
        dateField.setImmediate(true);
        dateField.setTimeZone(cal.getTimeZone());
        dateField.setLocale(EN);
        dateField.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                Date date = dateField.getValue();
                DateFormat format = DateFormat.getDateTimeInstance(
                        DateFormat.SHORT, DateFormat.LONG, EN);
                format.setTimeZone(UTC);
                log.log("Date changed to " + format.format(date));
            }
        });

        addComponent(timezoneSelector);
        addComponent(log);
        addComponent(dateField);
    }

    @Override
    protected String getDescription() {
        return "Tests the operation of the date field with different time zones";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(6066);
    }

}
