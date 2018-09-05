package com.vaadin.v7.tests.components.tree;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Test for keyboard navigation in tree in case when there are no items to
 * navigate.
 *
 * @author Vaadin Ltd
 */
public class TreeKeyboardNavigationToNoneTest extends MultiBrowserTest {

    @Before
    public void setUp() {
        setDebug(true);
        openTestURL();
    }

    @Test
    public void navigateUpForTheFirstItem() {
        sendKey(Keys.ARROW_UP);
        checkNotificationErrorAbsence("first");
    }

    @Test
    public void navigateDownForTheLastItem() {
        $(ButtonElement.class).first().click();
        sendKey(Keys.ARROW_DOWN);
        checkNotificationErrorAbsence("last");
    }

    private void checkNotificationErrorAbsence(String item) {
        assertFalse(
                "Notification is found after using keyboard for navigation "
                        + "from " + item + " tree item",
                isElementPresent(By.className("v-Notification")));
    }

    private void sendKey(Keys key) {
        Actions actions = new Actions(getDriver());
        actions.sendKeys(key).build().perform();
    }
}
