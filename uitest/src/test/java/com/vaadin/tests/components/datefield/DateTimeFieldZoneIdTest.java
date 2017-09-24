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
import java.time.ZoneId;
import java.util.TimeZone;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elementsbase.AbstractElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DateTimeFieldZoneIdTest extends MultiBrowserTest {

    private static TimeZone defaultTimeZone;

    @BeforeClass
    public static void init() {
        defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Brazil/Acre"));
    }

    @AfterClass
    public static void cleanup() {
        TimeZone.setDefault(defaultTimeZone);
    }

    @Test
    public void defaultDisplayName() {
        openTestURL();

        AbstractDateFieldElement dateField = $(AbstractDateFieldElement.class)
                .first();
        openDatePicker(dateField);

        LocalDate initialDate = INITIAL_DATE_TIME.toLocalDate();
        assertEndsWith(dateField, getUTCString(initialDate));

        goToNextMonth(6);
        selectDay(30);

        LocalDate thirtyOfJuly = initialDate.plus(6, MONTHS).withDayOfMonth(30);

        assertEndsWith(dateField, getUTCString(thirtyOfJuly));
    }

    @Test
    public void zoneIdTokyo() {
        openTestURL();

        AbstractDateFieldElement dateField = $(AbstractDateFieldElement.class)
                .first();

        setZoneId("Asia/Tokyo");

        openDatePicker(dateField);

        assertEndsWith(dateField, "JST");

        goToNextMonth(6);
        selectDay(30);

        assertEndsWith(dateField, "JST");
    }

    @Test
    public void zoneIdBerlin() {
        openTestURL();

        AbstractDateFieldElement dateField = $(AbstractDateFieldElement.class)
                .first();

        setZoneId("Europe/Berlin");

        openDatePicker(dateField);

        assertEndsWith(dateField, "CET");

        goToNextMonth(6);
        selectDay(30);

        assertEndsWith(dateField, "CEST");
    }

    @Test
    public void defaultDisplayNameLocaleGerman() {
        openTestURL();

        setLocale("de");

        AbstractDateFieldElement dateField = $(AbstractDateFieldElement.class)
                .first();
        openDatePicker(dateField);

        LocalDate initialDate = INITIAL_DATE_TIME.toLocalDate();
        assertEndsWith(dateField, getUTCString(initialDate));

        goToNextMonth(6);
        selectDay(30);

        LocalDate thirtyOfJuly = initialDate.plus(6, MONTHS).withDayOfMonth(30);

        assertEndsWith(dateField, getUTCString(thirtyOfJuly));
    }

    @Test
    public void zoneIdBeirutLocaleGerman() {
        openTestURL();

        AbstractDateFieldElement dateField = $(AbstractDateFieldElement.class)
                .first();

        setZoneId("Asia/Beirut");
        setLocale("de");

        openDatePicker(dateField);

        assertEndsWith(dateField, "OEZ");

        goToNextMonth(6);
        selectDay(30);

        assertEndsWith(dateField, "OESZ");
    }

    @Test
    public void zInQuotes() {
        openTestURL();

        AbstractDateFieldElement dateField = $(AbstractDateFieldElement.class)
                .first();

        setZoneId("Asia/Tokyo");

        TextFieldElement patternField = $(TextFieldElement.class)
                .id(PATTERN_ID);
        patternField.setValue("dd MMM yyyy - hh:mm:ss a 'z' z");

        openDatePicker(dateField);

        assertEndsWith(dateField, "z JST");

        goToNextMonth(6);
        selectDay(30);

        assertEndsWith(dateField, "z JST");
    }

    private void selectDay(int day) {
        for (WebElement e : findElements(
                By.className("v-datefield-calendarpanel-day"))) {
            if (e.getText().equals(String.valueOf(day))) {
                e.click();
                break;
            }
        }
    }

    private void openDatePicker(AbstractDateFieldElement dateField) {
        dateField.findElement(By.tagName("button")).click();
    }

    private void assertEndsWith(AbstractElement id, String suffix) {
        String text = id.findElement(By.xpath("./input")).getAttribute("value");
        assertTrue(text + " should end with " + suffix, text.endsWith(suffix));
    }

    private void goToNextMonth(int monthCount) {
        WebElement nextMonthButton = driver
                .findElement(By.className("v-button-nextmonth"));

        Actions actions = new Actions(driver);
        for (int i = 0; i < monthCount; i++) {
            actions.click(nextMonthButton);
        }
        actions.perform();
    }

    /**
     * Returns the timezone name formatted as returned by
     * {@link com.google.gwt.i18n.client.DateTimeFormat}, which supports only
     * standard GMT and RFC format.
     *
     * The {@link ZoneId} used is the operating system default
     */
    private static String getUTCString(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay()
                .atZone(defaultTimeZone.toZoneId()).toInstant();
        Duration duration = Duration
                .ofMillis(defaultTimeZone.getOffset(instant.toEpochMilli()));

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
