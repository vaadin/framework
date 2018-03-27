package com.vaadin.tests.navigator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NavigatorListenerModifiesListenersTest extends SingleBrowserTest {

    @Test
    public void testIfConfirmBack() {
        openTestURL();

        // keep URL of main view
        final String initialUrl = driver.getCurrentUrl();

        // do it 2 times to verify that this is not broken after first time
        for (int i = 0; i < 2; i++) {
            // go to prompted view
            WebElement button = $(ButtonElement.class).first();
            button.click();

            // verify we are in another view and url is correct
            waitForElementPresent(By.id(
                    NavigatorListenerModifiesListeners.LABEL_ANOTHERVIEW_ID));
            String currentUrl = driver.getCurrentUrl();
            assertEquals("Current URL should be equal to another view URL",
                    initialUrl.replace(
                            NavigatorListenerModifiesListeners.MainView.NAME,
                            NavigatorListenerModifiesListeners.AnotherView.NAME),
                    currentUrl);

            // click back button
            driver.navigate().back();

            // verify we are in main view and url is correct
            // without the fix for #17477, we get
            // ConcurrentModificationException
            waitForElementPresent(By
                    .id(NavigatorListenerModifiesListeners.LABEL_MAINVIEW_ID));
            currentUrl = driver.getCurrentUrl();
            assertEquals("Current URL should be equal to the initial view URL",
                    initialUrl, currentUrl);
        }
    }

}
