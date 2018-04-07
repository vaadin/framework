/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.datefield;

import static com.vaadin.tests.components.datefield.DateTimeFieldZoneId.INITIAL_DATE_TIME;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneId.LOCALE_ID;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneId.PATTERN_ID;
import static com.vaadin.tests.components.datefield.DateTimeFieldZoneId.ZONE_ID;
import static java.time.temporal.ChronoUnit.MONTHS;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldZoneIdTest extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // PhantomJS doesn't support getting timezone
        return getBrowsersExcludingPhantomJS();
    }

    private static LocalDateTime THIRTY_OF_JULY = INITIAL_DATE_TIME
            .plus(6, MONTHS).withDayOfMonth(30);

    @Test
    public void defaultDisplayName() {
        openTestURL();

        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        dateField.openPopup();

        LocalDate initialDate = INITIAL_DATE_TIME.toLocalDate();
        assertEndsWith(dateField, getUTCString(initialDate));

        dateField.setDateTime(THIRTY_OF_JULY);

        assertEndsWith(dateField, getUTCString(THIRTY_OF_JULY.toLocalDate()));
    }

    @Test
    public void zoneIdTokyo() {
        openTestURL();

        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();

        setZoneId("Asia/Tokyo");

        dateField.openPopup();

        assertEndsWith(dateField, "JST");

        dateField.setDateTime(THIRTY_OF_JULY);

        assertEndsWith(dateField, "JST");
    }

    @Test
    public void zoneIdBerlin() {
        openTestURL();

        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();

        setZoneId("Europe/Berlin");

        dateField.openPopup();

        assertEndsWith(dateField, "CET");

        dateField.setDateTime(THIRTY_OF_JULY);

        assertEndsWith(dateField, "CEST");
    }

    @Test
    public void defaultDisplayNameLocaleGerman() {
        openTestURL();

        setLocale("de");

        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();
        dateField.openPopup();

        assertEndsWith(dateField,
                getUTCString(INITIAL_DATE_TIME.toLocalDate()));

        dateField.setDateTime(THIRTY_OF_JULY);

        assertEndsWith(dateField, getUTCString(THIRTY_OF_JULY.toLocalDate()));
    }

    @Test
    public void zoneIdBeirutLocaleGerman() {
        openTestURL();

        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();

        setZoneId("Asia/Beirut");
        setLocale("de");

        dateField.openPopup();

        assertEndsWith(dateField, "OEZ");

        dateField.setDateTime(THIRTY_OF_JULY);

        assertEndsWith(dateField, "OESZ");
    }

    @Test
    public void zInQuotes() {
        openTestURL();

        DateTimeFieldElement dateField = $(DateTimeFieldElement.class).first();

        setZoneId("Asia/Tokyo");

        TextFieldElement patternField = $(TextFieldElement.class)
                .id(PATTERN_ID);
        patternField.setValue("dd MMM yyyy - hh:mm:ss a 'z' z");

        dateField.openPopup();

        assertEndsWith(dateField, "z JST");

        dateField.setDateTime(THIRTY_OF_JULY);

        assertEndsWith(dateField, "z JST");
    }

    private void assertEndsWith(DateTimeFieldElement element, String suffix) {
        String text = element.getValue();
        assertTrue(text + " should end with " + suffix, text.endsWith(suffix));
    }

    /**
     * Returns the timezone name formatted as returned by
     * {@link com.google.gwt.i18n.client.DateTimeFormat}, which supports only
     * standard GMT and RFC format.
     *
     * The {@link ZoneId} used is the operating system default
     */
    private String getUTCString(LocalDate localDate) {
        // Get the TimeZone from browser
        String browserTimeZone = ((JavascriptExecutor) getDriver())
                .executeScript(
                        "return Intl.DateTimeFormat().resolvedOptions().timeZone;")
                .toString();

        TimeZone timeZone = TimeZone.getTimeZone(browserTimeZone);
        Instant instant = localDate.atStartOfDay().atZone(timeZone.toZoneId())
                .toInstant();
        Duration duration = Duration
                .ofMillis(timeZone.getOffset(instant.toEpochMilli()));

        String suffix;
        if (duration.toMinutes() == 0) {
            suffix = "";
        } else {
            long minutes = duration.toMinutes()
                    % Duration.ofHours(1).toMinutes();
            long hours = duration.toHours();
            suffix = (hours >= 0 ? "+" : "") + hours
                    + (minutes != 0 ? ":" + minutes : "");
        }

        return "UTC" + suffix;
    }

    private void setZoneId(String zoneId) {
        ComboBoxElement zoneIdComboBox = $(ComboBoxElement.class).id(ZONE_ID);
        zoneIdComboBox.selectByText(zoneId);
    }

    private void setLocale(String locale) {
        ComboBoxElement zoneIdComboBox = $(ComboBoxElement.class).id(LOCALE_ID);
        zoneIdComboBox.selectByText(locale);
    }
}
