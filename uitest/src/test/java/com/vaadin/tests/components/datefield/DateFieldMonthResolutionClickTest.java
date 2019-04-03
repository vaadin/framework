package com.vaadin.tests.components.datefield;

import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import java.time.LocalDate;

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
        findElement(By.className("v-datefield-popup")).click();
        sleep(50);
        assertElementNotPresent(By.className("v-datefield-popup"));
        assertTrue("The selected year should be the current one",
                LocalDate.now().getYear() == Integer
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
        findElement(By.className("v-datefield-popup")).click();
        sleep(50);
        assertElementNotPresent(By.className("v-datefield-popup"));
        LocalDate now = LocalDate.now();
        String dateValue = new StringBuilder().append(now.getMonth().getValue())
                .append("/").append(now.getYear()).toString();
        assertTrue("The selected year should be the current one",
                dateValue.equals(monthResolutionDF.getValue()));
    }
}
