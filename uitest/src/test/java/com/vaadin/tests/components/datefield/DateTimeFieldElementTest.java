package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.testbench.elements.InlineDateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateTimeFieldElementTest extends SingleBrowserTest {

    @Test
    public void DateTimeFieldElementIsLocated() {
        openTestURL();

        assertThat($(DateTimeFieldElement.class).all().size(), is(3));
        assertThat($(InlineDateTimeFieldElement.class).all().size(), is(1));
    }

    @Test
    public void setGetValue() {
        openTestURL();

        // No date set
        DateTimeFieldElement defaultInitiallyEmpty = $(
                DateTimeFieldElement.class).first();
        Assert.assertNull(defaultInitiallyEmpty.getDateTime());
        defaultInitiallyEmpty
                .setDateTime(DateTimeFieldElementUI.TEST_DATE_TIME);
        Assert.assertEquals(DateTimeFieldElementUI.TEST_DATE_TIME,
                defaultInitiallyEmpty.getDateTime());
        assertServerValue("Default date field",
                DateTimeFieldElementUI.TEST_DATE_TIME);

        DateTimeFieldElement fi = $(DateTimeFieldElement.class).id("fi");
        Assert.assertEquals(DateTimeFieldElementUI.TEST_DATE_TIME,
                fi.getDateTime());
        fi.setDateTime(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME,
                fi.getDateTime());
        assertServerValue("Finnish date field",
                DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME);

        DateTimeFieldElement us = $(DateTimeFieldElement.class).id("us");
        Assert.assertEquals(DateTimeFieldElementUI.TEST_DATE_TIME,
                us.getDateTime());
        us.setDateTime(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME,
                us.getDateTime());
        assertServerValue("US date field",
                DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME);
    }

    private void assertServerValue(String id, LocalDateTime testDateTime) {
        Assert.assertEquals(id + " value set to " + testDateTime.toString(),
                getLogRow(0));

    }

    @Override
    protected Class<?> getUIClass() {
        return DateTimeFieldElementUI.class;
    }
}
