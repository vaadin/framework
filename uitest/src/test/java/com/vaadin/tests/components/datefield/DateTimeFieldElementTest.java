package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.DateTimeFieldElement;
import com.vaadin.testbench.elements.InlineDateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateTimeFieldElementTest extends SingleBrowserTest {

    @Test
    public void dateFieldElementIsLocated() {
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

        DateTimeFieldElement fi = $(DateTimeFieldElement.class).id("fi");
        Assert.assertEquals(DateTimeFieldElementUI.TEST_DATE_TIME,
                fi.getDateTime());
        fi.setDateTime(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME,
                fi.getDateTime());

        DateTimeFieldElement us = $(DateTimeFieldElement.class).id("us");
        Assert.assertEquals(DateTimeFieldElementUI.TEST_DATE_TIME,
                us.getDateTime());
        us.setDateTime(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(DateTimeFieldElementUI.ANOTHER_TEST_DATE_TIME,
                us.getDateTime());
    }

    @Override
    protected Class<?> getUIClass() {
        return DateTimeFieldElementUI.class;
    }
}
