package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class VaadinRefreshServletTest extends SingleBrowserTest {

    @Test
    public void redirectWorksWithContextPath() {
        getDriver().get(getBaseURL() + "/vaadinrefresh/");
        waitUntil((WebDriver d) -> "Please login"
                .equals(findElement(By.tagName("body")).getText()));
        assertEquals(getBaseURL() + "/statictestfiles/login.html",
                getDriver().getCurrentUrl());
    }
}
