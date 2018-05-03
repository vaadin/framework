package com.vaadin.tests.components.page;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class PageTitleTest extends MultiBrowserTest {

    @Test
    public void nullTitle() {
        driver.get(getTestUrl());
        Assert.assertEquals(PageTitle.class.getName(), driver.getTitle());
    }

    @Test
    public void fooTitle() {
        driver.get(getTestUrl() + "?title=foo");
        Assert.assertEquals("foo", driver.getTitle());
    }
}
