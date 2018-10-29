package com.vaadin.tests.layoutmanager;

import org.junit.Test;

import com.vaadin.tests.tb3.SingleBrowserTest;
import org.openqa.selenium.Dimension;

public class ConcurrentModificationUITest extends SingleBrowserTest {

    @Test
    public void noExceptionWhenEnlarging() {
        getDriver().manage().window().setSize(new Dimension(100, 100));
        openTestURL("debug");
        getDriver().manage().window().setSize(new Dimension(200, 200));
        assertNoErrorNotifications();
    }
}
