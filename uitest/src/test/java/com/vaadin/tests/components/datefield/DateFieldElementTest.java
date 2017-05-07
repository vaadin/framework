package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertNull(defaultInitiallyEmpty.getDate());
        defaultInitiallyEmpty.setDate(DateFieldElementUI.TEST_DATE_TIME);
        Assert.assertEquals(DateFieldElementUI.TEST_DATE_TIME,
                defaultInitiallyEmpty.getDate());

        DateFieldElement fi = $(DateFieldElement.class).id("fi");
        Assert.assertEquals(DateFieldElementUI.TEST_DATE_TIME, fi.getDate());
        fi.setDate(DateFieldElementUI.ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(DateFieldElementUI.ANOTHER_TEST_DATE_TIME,
                fi.getDate());

        DateFieldElement us = $(DateFieldElement.class).id("us");
        Assert.assertEquals(DateFieldElementUI.TEST_DATE_TIME, us.getDate());
        us.setDate(DateFieldElementUI.ANOTHER_TEST_DATE_TIME);
        Assert.assertEquals(DateFieldElementUI.ANOTHER_TEST_DATE_TIME,
                us.getDate());
    }

    @Override
    protected Class<?> getUIClass() {
        return DateFieldElementUI.class;
    }
}
