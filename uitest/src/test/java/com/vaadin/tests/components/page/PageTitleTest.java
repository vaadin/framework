package com.vaadin.tests.components.page;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PageTitleTest extends MultiBrowserTest {

    @Test
    public void nullTitle() {
        driver.get(getTestUrl());
        assertEquals(PageTitle.class.getName(), driver.getTitle());
    }

    @Test
    public void fooTitle() {
        driver.get(getTestUrl() + "?title=foo");
        assertEquals("foo", driver.getTitle());
    }
}
