package com.vaadin.tests.layoutmanager;

import org.junit.Test;
import org.openqa.selenium.Dimension;

import com.vaadin.tests.tb3.SingleBrowserTest;

public class ConcurrentModificationUITest extends SingleBrowserTest {

    @Test
    public void noExceptionWhenEnlarging() {
        getDriver().manage().window().setSize(new Dimension(100, 100));
        openTestURL("debug");
        getDriver().manage().window().setSize(new Dimension(200, 200));
        assertNoErrorNotifications();
    }
}
