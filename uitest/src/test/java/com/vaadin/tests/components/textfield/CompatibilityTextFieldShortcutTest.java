package com.vaadin.tests.components.textfield;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CompatibilityTextFieldShortcutTest extends SingleBrowserTest {

    private static final String TEXTFIELD_VALUE = "input";
    private static final String NOTIFICATION = "Received F8: "
            + TEXTFIELD_VALUE;

    @Test
    public void focusAndPressF8() {
        openTestURL();

        WebElement textFieldText = findElement(By.tagName("input"));
        $(ButtonElement.class).first().click();

        waitForElementVisible(By.className("focus-label"));
        textFieldText.sendKeys(TEXTFIELD_VALUE);

        textFieldText.sendKeys(Keys.F8);

        assertEquals(NOTIFICATION,
                $(NotificationElement.class).first().getCaption());
    }
}
