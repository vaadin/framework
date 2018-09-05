package com.vaadin.tests.components.datefield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class DateTimeFieldTestTest extends MultiBrowserTest {

    @Test
    public void testMakingRequired() throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(1000);
        menu("Component");
        menuSub("State");
        menu("Required");
        assertRequiredIndicatorVisible();
        assertNoErrorNotification();
    }

    private void assertRequiredIndicatorVisible() {
        getDriver().findElement(By.className("v-required-field-indicator"));
    }

    private void assertNoErrorNotification() {
        if (isElementPresent(NotificationElement.class)) {
            fail("Notification was present");
        }
    }

    @Test
    public void testValueAfterOpeningPopupInRequiredField()
            throws InterruptedException {
        setDebug(true);
        openTestURL();
        Thread.sleep(1000);
        menu("Component");
        menuSub("State");
        menu("Required");

        assertRequiredIndicatorVisible();

        menu("Component");
        menuSub("Features");
        menuSub("Resolution");
        menu("Month");

        menu("Component");
        menuSub("Listeners");
        menu("Value change listener");

        String inputtedValue = "2/12";
        getInput().sendKeys(inputtedValue);

        openPopup();
        closePopup();
        String actual = getInput().getAttribute("value");
        assertEquals(inputtedValue, actual);
        assertNoErrorNotification();

    }

    private void openPopup() throws InterruptedException {
        Dimension size = getInput().getSize();
        new Actions(getDriver()).moveToElement(getInput(), 0, 0)
                .moveByOffset(size.getWidth() + 5, size.getHeight() / 2)
                .click();
        // This fails in Opera for some weird reason
        // getDriver().findElement(By.className("v-datefield-button")).click();
    }

    private WebElement getInput() {
        return getDriver().findElement(By.xpath("//input"));
    }

    private void closePopup() {
        getDriver().findElement(By.tagName("body")).click();
    }

    private void menuSub(String string) {
        getDriver().findElement(By.xpath("//span[text() = '" + string + "']"))
                .click();
        new Actions(getDriver()).moveByOffset(100, 0).build().perform();
    }

    private void menu(String string) {
        getDriver().findElement(By.xpath("//span[text() = '" + string + "']"))
                .click();

    }

}
