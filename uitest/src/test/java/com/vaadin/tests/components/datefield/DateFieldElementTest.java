package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.testbench.elements.InlineDateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldElementTest extends SingleBrowserTest {

    @Test
    public void dateFieldElementIsLocated() {
        openTestURL();

        assertThat($(DateFieldElement.class).all().size(), is(3));
        assertThat($(InlineDateFieldElement.class).all().size(), is(1));
    }

    @Test
    public void setGetValue() {
        openTestURL();

        // No date set
        DateFieldElement defaultInitiallyEmpty = $(DateFieldElement.class)
                .first();
        assertNull(defaultInitiallyEmpty.getDate());
        defaultInitiallyEmpty.setDate(DateFieldElementUI.TEST_DATE_TIME);
        assertEquals(DateFieldElementUI.TEST_DATE_TIME,
                defaultInitiallyEmpty.getDate());
        assertServerValue("Default date field",
                DateFieldElementUI.TEST_DATE_TIME);

        DateFieldElement fi = $(DateFieldElement.class).id("fi");
        assertEquals(DateFieldElementUI.TEST_DATE_TIME, fi.getDate());
        fi.setDate(DateFieldElementUI.ANOTHER_TEST_DATE_TIME);
        assertEquals(DateFieldElementUI.ANOTHER_TEST_DATE_TIME, fi.getDate());
        assertServerValue("Finnish date field",
                DateFieldElementUI.ANOTHER_TEST_DATE_TIME);

        DateFieldElement us = $(DateFieldElement.class).id("us");
        assertEquals(DateFieldElementUI.TEST_DATE_TIME, us.getDate());
        us.setDate(DateFieldElementUI.ANOTHER_TEST_DATE_TIME);
        assertEquals(DateFieldElementUI.ANOTHER_TEST_DATE_TIME, us.getDate());
        assertServerValue("US date field",
                DateFieldElementUI.ANOTHER_TEST_DATE_TIME);
    }

    @Test
    public void testDateStyles() {
        openTestURL();
        assertTrue(findElements(By.className("teststyle")).isEmpty());

        $(ButtonElement.class).first().click();

        WebElement styledDateCell = $(InlineDateFieldElement.class).first()
                .findElement(By.className("teststyle"));
        assertEquals(String.valueOf(LocalDate.now().getDayOfMonth()),
                styledDateCell.getText());

        DateFieldElement fi = $(DateFieldElement.class).id("fi");
        fi.openPopup();
        waitForElementPresent(By.className("v-datefield-popup"));
        WebElement popup = findElement(
                com.vaadin.testbench.By.className("v-datefield-popup"));
        styledDateCell = popup.findElement(By.className("teststyle"));
        assertEquals("1", styledDateCell.getText());

        styledDateCell.click(); // close popup
        waitForElementNotPresent(By.className("v-datefield-popup"));

        DateFieldElement us = $(DateFieldElement.class).id("us");
        us.openPopup();
        waitForElementPresent(By.className("v-datefield-popup"));
        popup = findElement(
                com.vaadin.testbench.By.className("v-datefield-popup"));
        styledDateCell = popup.findElement(By.className("teststyle"));
        assertEquals("1", styledDateCell.getText());
    }

    private void assertServerValue(String id, LocalDate testDateTime) {
        assertEquals(id + " value set to " + testDateTime, getLogRow(0));

    }

    @Override
    protected Class<?> getUIClass() {
        return DateFieldElementUI.class;
    }
}
