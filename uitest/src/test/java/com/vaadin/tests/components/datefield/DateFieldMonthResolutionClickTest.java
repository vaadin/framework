package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertTrue;

public class DateFieldMonthResolutionClickTest extends MultiBrowserTest {

    @Before
    public void setUp() throws Exception {
        super.setup();
        openTestURL();
    }

    @Test
    public void testClickChangeValueYear() {
        DateFieldElement yearResolutionDF = $(DateFieldElement.class)
                .id("yearResolutionDF");
        yearResolutionDF.openPopup();
        assertTrue("Initially there should be no value",
                yearResolutionDF.getValue() == null
                        || yearResolutionDF.getValue().isEmpty());
        findElement(By.className("v-datefield-calendarpanel-month")).click();
        sleep(150);
        assertElementNotPresent(By.className("v-datefield-popup"));
        assertTrue("The selected year should be the current one",
                getZonedDateTimeAtECT().getYear() == Integer
                        .valueOf(yearResolutionDF.getValue()));
    }

    @Test
    public void testClickChangeValueMonth() {
        DateFieldElement monthResolutionDF = $(DateFieldElement.class)
                .id("monthResolutionDF");
        monthResolutionDF.openPopup();
        assertTrue(
                String.format("Initially there should be no value, but was %s",
                        monthResolutionDF.getValue()),
                monthResolutionDF.getValue() == null
                        || monthResolutionDF.getValue().isEmpty());
        findElement(By.className("v-datefield-calendarpanel-month")).click();
        sleep(150);
        assertElementNotPresent(By.className("v-datefield-popup"));
        String dateValue = new StringBuilder()
                .append(getZonedDateTimeAtECT().getMonth().getValue())
                .append("/").append(getZonedDateTimeAtECT().getYear())
                .toString();
        assertTrue("The selected year should be the current one",
                dateValue.equals(monthResolutionDF.getValue()));
    }

    @Test
    public void testResolutionDayHeaderNotClickable() {
        DateFieldElement dayResolutionDF = $(DateFieldElement.class)
                .id("resolutionDayDF");
        dayResolutionDF.openPopup();
        sleep(100);
        assertElementPresent(By.className("v-datefield-popup"));
        findElement(By.className("v-datefield-calendarpanel-month")).click();
        // Click should have no effect
        assertElementPresent(By.className("v-datefield-popup"));

    }

    @Test
    public void setResoultionToYearAndClick() {
        // Switch the resolution to verify clicking is now enabled
        findElement(By.id("buttonChangeResolution")).click();
        sleep(200);
        $(DateFieldElement.class).id("resolutionDayDF").openPopup();
        sleep(100);
        assertElementPresent(By.className("v-datefield-popup"));
        findElement(By.className("v-datefield-calendarpanel-month")).click();
        sleep(150);
        assertElementNotPresent(By.className("v-datefield-popup"));
        // Set Back to month
        findElement(By.id("buttonChangeResolution")).click();
    }

    private ZonedDateTime getZonedDateTimeAtECT() {
        return ZonedDateTime.now(ZoneId.of("Europe/Paris"));
    }
}
